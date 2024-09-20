package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.dao.EmpleadoDao;
import com.fiuni.distri.project.fiuni.dao.EvaluacionDao;
import com.fiuni.distri.project.fiuni.domain.Empleado;
import com.fiuni.distri.project.fiuni.domain.Evaluacion;
import com.fiuni.distri.project.fiuni.dto.EvaluacionDto;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import com.fiuni.distri.project.fiuni.specifications.EvaluacionSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionDao evaluacionDao;

    @Autowired
    private EmpleadoDao empleadoDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CacheRedisService<EvaluacionDto> cacheRedisService;

    // Método para guardar una nueva evaluación y retornar un DTO
    @CachePut(value = "evaluacion", key = "#result.id")
    public EvaluacionDto guardarEvaluacion(EvaluacionDto evaluacionDto) {
        Evaluacion evaluacion = modelMapper.map(evaluacionDto, Evaluacion.class);
        Empleado empleado = empleadoDao.findById(evaluacionDto.getEmpleado_id())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluacion no encontrada con id " + evaluacionDto.getEmpleado_id()));
        evaluacion.setEmpleado(empleado);
        Evaluacion savedEvaluacion = evaluacionDao.save(evaluacion);
        return mapToDto(savedEvaluacion);
    }

    // Método para actualizar una evaluación
    @CacheEvict(value = "evaluacion", key = "#id")
    @CachePut(value = "evaluacion", key = "#id")
    public EvaluacionDto actualizarEvaluacion(Integer id, EvaluacionDto evaluacionDto) {
        Evaluacion evaluacionExistente = evaluacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con id " + id));

        // Actualizar solo los campos que no son nulos
        if (evaluacionDto.getFecha() != null) {
            evaluacionExistente.setFecha(evaluacionDto.getFecha());
        }
        if (evaluacionDto.getDescripcion() != null) {
            evaluacionExistente.setDescripcion(evaluacionDto.getDescripcion());
        }
        if (evaluacionDto.getPuntaje_general() != 0) {
            evaluacionExistente.setPuntaje_general(BigDecimal.valueOf(evaluacionDto.getPuntaje_general()));
        }
        if (evaluacionDto.isPendiente()) {
            evaluacionExistente.setPendiente(evaluacionDto.isPendiente());
        }
        if (evaluacionDto.getEmpleado_id() != 0) {
            Empleado empleado = empleadoDao.findById(evaluacionDto.getEmpleado_id())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con el ID: " + evaluacionDto.getEmpleado_id()));
            evaluacionExistente.setEmpleado(empleado);
        }

        Evaluacion evaluacionActualizada = evaluacionDao.save(evaluacionExistente);
        return mapToDto(evaluacionActualizada);
    }

    // Método para obtener una evaluación por ID
    @Cacheable(value = "evaluacion", key = "#id")
    public EvaluacionDto obtenerPorId(int id) {
        Optional<Evaluacion> evaluacion = evaluacionDao.findById(id);
        if (evaluacion.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Evaluación no encontrada con id " + id);
        }
        return mapToDto(evaluacion.get());
    }

    // Método para obtener evaluaciones con especificaciones dinámicas, paginación y caché
    public Page<EvaluacionDto> obtenerTodos(Specification<Evaluacion> spec, Pageable pageable) {
        Page<Evaluacion> evaluacionPage = evaluacionDao.findAll(spec, pageable);
        Page<EvaluacionDto> evaluacionDtos = evaluacionPage.map(this::mapToDto);

        // Guardar en caché cada evaluación obtenida
        evaluacionDtos.forEach(evaluacionDto ->
                cacheRedisService.setWithDefaultTTL("evaluacion", "" + evaluacionDto.getId(), evaluacionDto)
        );

        return evaluacionDtos;
    }

    // Método para eliminar una evaluación por ID
    @CacheEvict(value = "evaluacion", key = "#id")
    public String eliminarPorId(int id) {
        if (!evaluacionDao.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Evaluación no encontrada con id " + id);
        }
        evaluacionDao.deleteById(id);
        return "Evaluación con ID " + id + " eliminada correctamente";
    }

    // Método para obtener evaluaciones filtradas por varios criterios
    /*public Page<EvaluacionDto> obtenerTodos(BigDecimal puntaje, String descripcion, Integer empleadoId, Boolean pendiente, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Evaluacion> specification = Specification
                .where(EvaluacionSpecification.hasDescripcion(descripcion))
                .and(EvaluacionSpecification.hasPuntaje(puntaje))
                .and(EvaluacionSpecification.hasEmpleadoId(empleadoId))
                .and(EvaluacionSpecification.isPendiente(pendiente));
        Page<Evaluacion> evaluacionPage = evaluacionDao.findAll(specification, pageable);
        return evaluacionPage.map(this::mapToDto);
    }*/

    // Función para mapear de Evaluacion a EvaluacionDto
    public EvaluacionDto mapToDto(Evaluacion evaluacion) {
        EvaluacionDto evaluacionDto = new EvaluacionDto();
        modelMapper.map(evaluacion, evaluacionDto);
        if (evaluacion.getEmpleado() != null) {
            evaluacionDto.setEmpleado_id(evaluacion.getEmpleado().getId());
        }
        return evaluacionDto;
    }

}
