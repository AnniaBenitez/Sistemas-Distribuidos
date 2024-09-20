package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.domain.Evaluacion;
import com.fiuni.distri.project.fiuni.dto.EvaluacionDto;
import com.fiuni.distri.project.fiuni.service.EvaluacionService;
import com.fiuni.distri.project.fiuni.specifications.EvaluacionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/evaluacion")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    // Guardar evaluación
    @PostMapping
    public ResponseEntity<EvaluacionDto> guardar(@RequestBody EvaluacionDto evaluacionDto) {
        EvaluacionDto savedDto = evaluacionService.guardarEvaluacion(evaluacionDto);
        return ResponseEntity.ok(savedDto);
    }

    // Obtener evaluación por ID con manejo de excepciones
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDto> obtenerPorId(@PathVariable int id) {
        EvaluacionDto dto = evaluacionService.obtenerPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<EvaluacionDto>> obtenerTodos(
            @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam(name = "descripcion", required = false) String descripcion,
            @RequestParam(name = "puntaje_general", required = false) BigDecimal puntajeGeneral,
            @RequestParam(name = "pendiente", required = false) Boolean pendiente,
            @RequestParam(name = "empleado_id", required = false) Integer empleadoId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Crear especificaciones dinámicamente
        Specification<Evaluacion> specification = Specification
                .where(EvaluacionSpecification.hasDescripcion(descripcion))
                .and(EvaluacionSpecification.hasPuntaje(puntajeGeneral))
                .and(EvaluacionSpecification.hasEmpleadoId(empleadoId))
                .and(EvaluacionSpecification.isPendiente(pendiente));

        // Llamar al servicio con las especificaciones y paginación
        Page<EvaluacionDto> dtos = evaluacionService.obtenerTodos(specification, pageable);
        return ResponseEntity.ok(dtos);
    }

    // Actualizar evaluación por ID
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionDto> actualizar(@PathVariable int id, @RequestBody EvaluacionDto evaluacionDto) {
        EvaluacionDto actualizado = evaluacionService.actualizarEvaluacion(id, evaluacionDto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar evaluación por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEvaluacion(@PathVariable int id) {
        String respuesta = evaluacionService.eliminarPorId(id);
        return ResponseEntity.ok(respuesta);
    }
}
