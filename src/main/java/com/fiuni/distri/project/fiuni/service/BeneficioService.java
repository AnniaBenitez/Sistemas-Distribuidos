package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.dao.BeneficioDao;
import com.fiuni.distri.project.fiuni.domain.Beneficio;
import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.dto.BeneficioDetalleDto;
import com.fiuni.distri.project.fiuni.dto.BeneficioDto;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import java.util.Optional;

@Service
public class BeneficioService {

    @Autowired
    private BeneficioDao beneficioDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CacheRedisService<BeneficioDto> cacheRedisService;

    // Método para guardar un nuevo beneficio y retornar un DTO
    @CachePut(value = "beneficio", key = "#result.id")
    public BeneficioDto guardarBeneficio(BeneficioDto beneficioDTO) {
        Beneficio beneficio = modelMapper.map(beneficioDTO, Beneficio.class);
        Beneficio savedBeneficio = beneficioDao.save(beneficio);
        return modelMapper.map(savedBeneficio, BeneficioDto.class);
    }

    @CacheEvict(value = "beneficio", key = "#id")
    @CachePut(value = "beneficio", key = "#id")
    public BeneficioDto actualizarBeneficio(Integer id, BeneficioDto beneficioDTO) {
        // Paso 1: Obtener la entidad existente
        Beneficio beneficioExistente = beneficioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficio no encontrado con id " + id));

        // Paso 2: Actualizar los campos necesarios
        if (beneficioDTO.isActivo()) {
            beneficioExistente.setActivo(beneficioDTO.isActivo());
        }
        if (beneficioDTO.getFecha_fin() != null) {
            beneficioExistente.setFecha_fin(beneficioDTO.getFecha_fin());
        }
        if (beneficioDTO.getFecha_inicio() != null) {
            beneficioExistente.setFecha_inicio(beneficioDTO.getFecha_inicio());
        }
        if (beneficioDTO.getNombre() != null) {
            beneficioExistente.setNombre(beneficioDTO.getNombre());
        }
        if (beneficioDTO.getPorcentaje_de_sueldo() != null) {
            beneficioExistente.setPorcentaje_de_sueldo(beneficioDTO.getPorcentaje_de_sueldo());
        }
        // Guardar la entidad actualizada
        Beneficio actualizadoBeneficio = beneficioDao.save(beneficioExistente);

        // Retornar el DTO actualizado
        return modelMapper.map(actualizadoBeneficio, BeneficioDto.class);
    }

    // Método para obtener un beneficio por ID y retornar un DTO
    @Cacheable(value = "beneficio", key = "#id")
    public BeneficioDto obtenerPorId(int id) {
        Optional<Beneficio> beneficio = beneficioDao.findById(id);
        if (beneficio.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Beneficio no encontrado con id " + id);
        }
        return modelMapper.map(beneficio.get(), BeneficioDto.class);
    }


    // Método para obtener todos los beneficios con posibilidad de aplicar filtros y retornar una lista de DTOs
    public Page<BeneficioDto> obtenerTodos(Specification<Beneficio> spec, Pageable pageable) {

        Page<Beneficio> beneficios = beneficioDao.findAll(spec, pageable);
        Page<BeneficioDto> beneficioDto = beneficios.map(entity -> modelMapper.map(entity, BeneficioDto.class));
        beneficioDto.forEach(beneficioDtos ->
                cacheRedisService.setWithDefaultTTL("beneficio", "" + beneficioDtos.getId(), beneficioDtos)
        );
        return beneficioDto;
    }

    // Método para eliminar un beneficio por ID
    @CacheEvict(value = "beneficio", key = "#id")
    public String eliminarPorId(int id) {
        if (!beneficioDao.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Beneficio no encontrado con id " + id);
        }
        beneficioDao.deleteById(id);
        return "Beneficio con ID " + id + " eliminado correctamente";
    }
}
