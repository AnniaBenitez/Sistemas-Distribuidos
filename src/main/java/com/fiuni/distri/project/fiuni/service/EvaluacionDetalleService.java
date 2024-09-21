package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.dao.EvaluacionDao;
import com.fiuni.distri.project.fiuni.dao.EvaluacionDetalleDao;
import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.domain.Evaluacion;
import com.fiuni.distri.project.fiuni.domain.EvaluacionDetalle;
import com.fiuni.distri.project.fiuni.dto.BeneficioDetalleDto;
import com.fiuni.distri.project.fiuni.dto.EvaluacionDetalleDto;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;


@Service
public class EvaluacionDetalleService {

    @Autowired
    private EvaluacionDetalleDao evaluacionDetalleDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EvaluacionDao evaluacionDao;

    @Autowired
    CacheRedisService<EvaluacionDetalleDto> cacheRedisService;

    // Método para guardar un nuevo detalle de evaluación y retornar un DTO
    @CachePut(value = "evaluacionDetalle", key = "#result.id")
    public EvaluacionDetalleDto guardarEvaluacionDetalle(EvaluacionDetalleDto evaluacionDetalleDto) {
        Evaluacion evaluacion = evaluacionDao.findById(evaluacionDetalleDto.getEvaluacion_id())
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada con el ID: " + evaluacionDetalleDto.getEvaluacion_id()));

        EvaluacionDetalle evaluacionDetalle = modelMapper.map(evaluacionDetalleDto, EvaluacionDetalle.class);
        evaluacionDetalle.setEvaluacion(evaluacion);
        EvaluacionDetalle savedEvaluacionDetalle = evaluacionDetalleDao.save(evaluacionDetalle);
        return mapToDto(savedEvaluacionDetalle);
    }

    // Método para obtener un detalle de evaluación por ID y retornar un DTO
    @Cacheable(value = "evaluacionDetalle", key = "#id")
    public EvaluacionDetalleDto obtenerPorId(int id) {
        EvaluacionDetalle evaluacionDetalle = evaluacionDetalleDao.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Detalle de Evaluación no encontrado con id " + id));

        return mapToDto(evaluacionDetalle);
    }

    // Método para obtener todos los detalles de evaluación con paginación y filtrado dinámico
    public Page<EvaluacionDetalleDto> obtenerTodos(Specification<EvaluacionDetalle> spec, Pageable pageable) {
        Page<EvaluacionDetalle> evaluacionDetallePage = evaluacionDetalleDao.findAll(spec, pageable);
        Page<EvaluacionDetalleDto> evaluacionDetalleDtos = evaluacionDetallePage.map(this::mapToDto);
        evaluacionDetalleDtos.forEach(evaluacionDetalleDto ->
                cacheRedisService.setWithDefaultTTL("EvaluacionDetalle", "" + evaluacionDetalleDto.getId(), evaluacionDetalleDto)
        );
        return evaluacionDetalleDtos;
    }



    // Método para obtener todos los detalles de evaluación con paginación y filtrado dinámico
    /*public Page<EvaluacionDetalleDto> obtenerTodos(Integer evaluacionId, String criterio, Integer puntaje, String comentarios, Pageable pageable) {
        // Crear especificaciones dinámicamente
        Specification<EvaluacionDetalle> specification = Specification
                .where(EvaluacionDetalleSpecification.hasEvaluacionId(evaluacionId))
                .and(EvaluacionDetalleSpecification.hasCriterio(criterio))
                .and(EvaluacionDetalleSpecification.hasPuntaje(puntaje))
                .and(EvaluacionDetalleSpecification.hasComentarios(comentarios));

        // Obtener la página filtrada
        Page<EvaluacionDetalle> evaluacionDetallePage = evaluacionDetalleDao.findAll(specification, pageable);
        return evaluacionDetallePage.map(this::mapToDto);
    }*/


    // Método para eliminar un detalle de evaluación por ID
    @CacheEvict(value = "evaluacionDetalle", key = "#id")
    public String eliminarPorId(int id) {
        if (!evaluacionDetalleDao.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Detalle de evaluación no encontrado con id " + id);
        }
        evaluacionDetalleDao.deleteById(id);
        return "Detalle de evaluación con ID " + id + " eliminado correctamente";
    }

    // Método para actualizar completamente un detalle de evaluación
    //@CacheEvict(value = "evaluacionDetalle", key = "#id")
    @CachePut(value = "evaluacionDetalle", key = "#id")
    public EvaluacionDetalleDto actualizarEvaluacionDetalle(Integer id, EvaluacionDetalleDto evaluacionDetalleDto) {
        EvaluacionDetalle evaluacionDetalleExistente = evaluacionDetalleDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de Evaluación no encontrado con id " + id));

        if(evaluacionDetalleDto.getCriterio() != null) {
            evaluacionDetalleExistente.setCriterio(evaluacionDetalleDto.getCriterio());
        }
        if(evaluacionDetalleDto.getComentarios() != null) {
            evaluacionDetalleExistente.setComentarios(evaluacionDetalleDto.getComentarios());
        }
        if(evaluacionDetalleDto.getPuntaje() != 0) {
            evaluacionDetalleExistente.setPuntaje(evaluacionDetalleDto.getPuntaje());
        }
        if (evaluacionDetalleDto.getEvaluacion_id() != 0) {
            Evaluacion evaluacion = evaluacionDao.findById(evaluacionDetalleDto.getEvaluacion_id())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Detalle de Evaluación no encontrada con id " + evaluacionDetalleDto.getEvaluacion_id()));
            evaluacionDetalleExistente.setEvaluacion(evaluacion);
        }
        EvaluacionDetalle evaluacionDetalleActualizado = evaluacionDetalleDao.save(evaluacionDetalleExistente);
        return mapToDto(evaluacionDetalleActualizado);
    }

    // Función para mapear de EvaluacionDetalle a EvaluacionDetalleDto
    private EvaluacionDetalleDto mapToDto(EvaluacionDetalle evaluacionDetalle) {
        EvaluacionDetalleDto evaluacionDetalleDto = new EvaluacionDetalleDto();

        // Mapear propiedades primitivas con ModelMapper
        modelMapper.map(evaluacionDetalle, evaluacionDetalleDto);

        // Asignar evaluacion_id manualmente desde la entidad relacionada Evaluacion, si aplica
        if (evaluacionDetalle.getEvaluacion() != null) {
            evaluacionDetalleDto.setEvaluacion_id(evaluacionDetalle.getEvaluacion().getId());
        }

        return evaluacionDetalleDto;
    }
}
