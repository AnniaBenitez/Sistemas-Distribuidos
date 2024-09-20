package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.domain.Beneficio;
import com.fiuni.distri.project.fiuni.dto.BeneficioDto;
import com.fiuni.distri.project.fiuni.service.BeneficioService;
import com.fiuni.distri.project.fiuni.specifications.BeneficioSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/beneficio")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @PostMapping
    public ResponseEntity<BeneficioDto> guardar(@RequestBody BeneficioDto beneficioDto) {
        BeneficioDto savedDto = beneficioService.guardarBeneficio(beneficioDto);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDto> obtenerPorId(@PathVariable int id) {
        BeneficioDto dto = beneficioService.obtenerPorId(id);  // Ahora devuelve directamente el DTO o lanza una excepción
        return ResponseEntity.ok(dto);  // Devolver el DTO con el código HTTP 200 OK
    }


    @GetMapping
    public ResponseEntity<Page<BeneficioDto>> obtenerTodos(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "porcentaje_de_sueldo", required = false) BigDecimal porcentajeDeSueldo,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @RequestParam(name = "fecha_inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(name = "fecha_fin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Crear la especificación con base en los parámetros
        Specification<Beneficio> specification = Specification
                .where(BeneficioSpecification.hasNombre(nombre))
                .and(BeneficioSpecification.hasPorcentajeDeSueldo(porcentajeDeSueldo))
                .and(BeneficioSpecification.isActivo(activo))
                .and(BeneficioSpecification.hasFechaInicio(fechaInicio))
                .and(BeneficioSpecification.hasFechaFin(fechaFin));

        // Llamar al servicio pasándole la especificación y el pageable
        Page<BeneficioDto> dtos = beneficioService.obtenerTodos(specification, pageable);

        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BeneficioDto> actualizar(@PathVariable Integer id, @RequestBody BeneficioDto beneficioDto) {
        BeneficioDto actualizado = beneficioService.actualizarBeneficio(id, beneficioDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/beneficio/{id}")
    public ResponseEntity<String> eliminarBeneficio(@PathVariable int id) {
        String respuesta = beneficioService.eliminarPorId(id);
        return ResponseEntity.ok(respuesta);
    }

}
