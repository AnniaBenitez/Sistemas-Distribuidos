package com.fiuni.distri.project.fiuni.controllers;

import com.fiuni.distri.project.fiuni.domain.EvaluacionDetalle;
import com.fiuni.distri.project.fiuni.dto.EvaluacionDetalleDto;
import com.fiuni.distri.project.fiuni.dto.ResponseDto;
import com.fiuni.distri.project.fiuni.service.EvaluacionDetalleService;
import com.fiuni.distri.project.fiuni.specifications.EvaluacionDetalleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluacion-detalle")
public class EvaluacionDetalleController {

    @Autowired
    private EvaluacionDetalleService evaluacionDetalleService;

    @PostMapping
    public ResponseEntity<EvaluacionDetalleDto> guardar(@RequestBody EvaluacionDetalleDto evaluacionDetalleDto) {
        EvaluacionDetalleDto savedDto = evaluacionDetalleService.guardarEvaluacionDetalle(evaluacionDetalleDto);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDetalleDto> obtenerPorId(@PathVariable int id) {
        EvaluacionDetalleDto dto = evaluacionDetalleService.obtenerPorId(id);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<ResponseDto<Page<EvaluacionDetalleDto>>> obtenerTodos(
            @RequestParam(name = "evaluacion_id", required = false) Integer evaluacionId,
            @RequestParam(name = "criterio", required = false) String criterio,
            @RequestParam(name = "puntaje", required = false) Integer puntaje,
            @RequestParam(name = "comentarios", required = false) String comentarios,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Crear especificaciones dinámicamente
        Specification<EvaluacionDetalle> specification = Specification
                .where(EvaluacionDetalleSpecification.hasEvaluacionId(evaluacionId))
                .and(EvaluacionDetalleSpecification.hasCriterio(criterio))
                .and(EvaluacionDetalleSpecification.hasPuntaje(puntaje))
                .and(EvaluacionDetalleSpecification.hasComentarios(comentarios));

        // Llamar al servicio con la specification
        Page<EvaluacionDetalleDto> dtos = evaluacionDetalleService.obtenerTodos(specification, pageable);

        return ResponseEntity.ok(new ResponseDto<>(200, true,"exisoto", dtos, null));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionDetalleDto> actualizar(@PathVariable Integer id, @RequestBody EvaluacionDetalleDto evaluacionDetalleDto) {
        EvaluacionDetalleDto actualizado = evaluacionDetalleService.actualizarEvaluacionDetalle(id, evaluacionDetalleDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEvaluacionDetalle(@PathVariable int id) {
        String respuesta = evaluacionDetalleService.eliminarPorId(id);
        return ResponseEntity.ok(respuesta);
    }

}
