package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.dao.BeneficioDao;
import com.fiuni.distri.project.fiuni.dao.BeneficioDetalleDao;
import com.fiuni.distri.project.fiuni.dao.EmpleadoDao;
import com.fiuni.distri.project.fiuni.domain.Beneficio;
import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.domain.Empleado;
import com.fiuni.distri.project.fiuni.dto.BeneficioDetalleDto;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;

@Service
public class BeneficioDetalleService {

    @Autowired
    private BeneficioDetalleDao beneficioDetalleDao;

    @Autowired
    private BeneficioDao beneficioDao;

    @Autowired
    private EmpleadoDao empleadoDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CacheRedisService<BeneficioDetalleDto> cacheRedisService;


    // Método para guardar un nuevo detalle de beneficio y retornar un DTO
    @CachePut(value = "beneficioDetalle", key = "#result.id")
    public BeneficioDetalleDto guardarBeneficioDetalle(BeneficioDetalleDto beneficioDetalleDTO) {
        Beneficio beneficio = beneficioDao.findById(beneficioDetalleDTO.getBeneficio_id())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Beneficio no encontrado con id " + beneficioDetalleDTO.getBeneficio_id()));

        Empleado empleado = empleadoDao.findById(beneficioDetalleDTO.getEmpleado_id())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Empleado no encontrado con id " + beneficioDetalleDTO.getEmpleado_id()));

        BeneficioDetalle beneficioDetalle = modelMapper.map(beneficioDetalleDTO, BeneficioDetalle.class);
        beneficioDetalle.setBeneficio(beneficio);
        beneficioDetalle.setEmpleado(empleado);
        BeneficioDetalle savedBeneficioDetalle = beneficioDetalleDao.save(beneficioDetalle);
        return mapToDto(savedBeneficioDetalle);
    }

    // Método para actualizar un detalle de beneficio
    @CacheEvict(value = "beneficioDetalle", key = "#id")
    @CachePut(value = "beneficioDetalle", key = "#id")
    public BeneficioDetalleDto actualizarBeneficioDetalle(int id, BeneficioDetalleDto beneficioDetalleDTO) {
        BeneficioDetalle beneficioDetalleExistente = beneficioDetalleDao.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BeneficioDetalle no encontrado con id " + id));

        if (beneficioDetalleDTO.getBeneficio_id() != 0) {
            Beneficio beneficio = beneficioDao.findById(beneficioDetalleDTO.getBeneficio_id())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Beneficio no encontrado con id " + beneficioDetalleDTO.getBeneficio_id()));
            beneficioDetalleExistente.setBeneficio(beneficio);
        }

        if (beneficioDetalleDTO.getEmpleado_id() != 0) {
            Empleado empleado = empleadoDao.findById(beneficioDetalleDTO.getEmpleado_id())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Empleado no encontrado con id " + beneficioDetalleDTO.getEmpleado_id()));
            beneficioDetalleExistente.setEmpleado(empleado);
        }

        if (beneficioDetalleDTO.getMonto() != 0.0) {
            beneficioDetalleExistente.setMonto(BigDecimal.valueOf(beneficioDetalleDTO.getMonto()));
        }

        if (beneficioDetalleDTO.isActivo()) {
            beneficioDetalleExistente.setActivo(beneficioDetalleDTO.isActivo());
        }

        BeneficioDetalle beneficioDetalleActualizado = beneficioDetalleDao.save(beneficioDetalleExistente);
        return mapToDto(beneficioDetalleActualizado);
    }

    // Método para obtener un detalle de beneficio por ID y retornar un DTO
    @Cacheable(value = "beneficioDetalle", key = "#id")
    public BeneficioDetalleDto obtenerPorId(int id) {
        BeneficioDetalle beneficioDetalle = beneficioDetalleDao.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BeneficioDetalle no encontrado con id " + id));
        return mapToDto(beneficioDetalle);
    }

    // Método para obtener todos los detalles de beneficios con paginación y filtros
    public Page<BeneficioDetalleDto> obtenerTodos(Specification<BeneficioDetalle> spec, Pageable pageable) {
        Page<BeneficioDetalle> beneficioDetalles = beneficioDetalleDao.findAll(spec, pageable);
        Page<BeneficioDetalleDto> beneficioDetalleDtos = beneficioDetalles.map(this::mapToDto);
        beneficioDetalleDtos.forEach(beneficioDetalleDto ->
                cacheRedisService.setWithDefaultTTL("beneficioDetalle", "" + beneficioDetalleDto.getId(), beneficioDetalleDto)
        );
        return beneficioDetalleDtos;
    }


    // Método para eliminar un detalle de beneficio por ID
    @CacheEvict(value = "beneficioDetalle", key = "#id")
    public String eliminarPorId(int id) {
        if (!beneficioDetalleDao.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "BeneficioDetalle no encontrado con id " + id);
        }
        beneficioDetalleDao.deleteById(id);
        return "BeneficioDetalle con id " + id + " eliminado correctamente.";
    }


    // Función para mapear de BeneficioDetalle a BeneficioDetalleDto
    private BeneficioDetalleDto mapToDto(BeneficioDetalle beneficioDetalle) {
        BeneficioDetalleDto beneficioDetalleDto = new BeneficioDetalleDto();
        modelMapper.map(beneficioDetalle, beneficioDetalleDto);

        if (beneficioDetalle.getBeneficio() != null) {
            beneficioDetalleDto.setBeneficio_id(beneficioDetalle.getBeneficio().getId());
        }

        if (beneficioDetalle.getEmpleado() != null) {
            beneficioDetalleDto.setEmpleado_id(beneficioDetalle.getEmpleado().getId());
        }

        return beneficioDetalleDto;
    }
}
