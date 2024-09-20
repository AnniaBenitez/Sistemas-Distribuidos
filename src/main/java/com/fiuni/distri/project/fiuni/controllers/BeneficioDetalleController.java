package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.dto.BeneficioDetalleDto;
import com.fiuni.distri.project.fiuni.service.BeneficioDetalleService;
import com.fiuni.distri.project.fiuni.specifications.BeneficioDetalleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/beneficio-detalle")
public class BeneficioDetalleController {

    @Autowired
    private BeneficioDetalleService beneficioDetalleService;

    @Autowired
    private BeneficioDetalleSpecification beneficioDetalleSpecification;

    @PostMapping
    public ResponseEntity<BeneficioDetalleDto> guardar(@RequestBody BeneficioDetalleDto beneficioDetalleDto) {
        BeneficioDetalleDto savedDto = beneficioDetalleService.guardarBeneficioDetalle(beneficioDetalleDto);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDetalleDto> obtenerPorId(@PathVariable int id) {
        BeneficioDetalleDto dto = beneficioDetalleService.obtenerPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<BeneficioDetalleDto>> obtenerTodos(
            @RequestParam(name = "beneficio_id", required = false) Integer beneficioId,
            @RequestParam(name = "monto", required = false) Double monto,
            @RequestParam(name = "empleado_id", required = false) Integer empleadoId,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<BeneficioDetalle> spec = Specification
                .where(beneficioDetalleSpecification.hasBeneficioId(beneficioId))
                .and(beneficioDetalleSpecification.hasMonto(monto))
                .and(beneficioDetalleSpecification.hasEmpleadoId(empleadoId))
                .and(beneficioDetalleSpecification.isActivo(activo));

        Page<BeneficioDetalleDto> dtos = beneficioDetalleService.obtenerTodos(spec, pageable);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficioDetalleDto> actualizar(@PathVariable int id, @RequestBody BeneficioDetalleDto beneficioDetalleDto) {
        BeneficioDetalleDto actualizado = beneficioDetalleService.actualizarBeneficioDetalle(id, beneficioDetalleDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarBeneficioDetalle(@PathVariable int id) {
        String resultado = beneficioDetalleService.eliminarPorId(id);
        return ResponseEntity.ok(resultado);
    }

}
