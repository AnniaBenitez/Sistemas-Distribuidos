package com.fiuni.distri.project.fiuni.specifications;

import com.fiuni.distri.project.fiuni.domain.Evaluacion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EvaluacionSpecification {

    // Especificación para filtrar por fecha
    public static Specification<Evaluacion> hasFecha(LocalDateTime fecha) {
        return (root, query, criteriaBuilder) -> {
            if (fecha == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("fecha"), fecha);
        };
    }

    // Especificación para filtrar por descripción
    public static Specification<Evaluacion> hasDescripcion(String descripcion) {
        return (root, query, criteriaBuilder) -> {
            if (descripcion == null || descripcion.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + descripcion.toLowerCase() + "%");
        };
    }

    // Especificación para filtrar por puntaje_general
    public static Specification<Evaluacion> hasPuntaje(BigDecimal puntajeGeneral) {
        return (root, query, criteriaBuilder) -> {
            if (puntajeGeneral == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("puntaje_general"), puntajeGeneral);
        };
    }

    // Especificación para filtrar por pendiente
    public static Specification<Evaluacion> isPendiente(Boolean pendiente) {
        return (root, query, criteriaBuilder) -> {
            if (pendiente == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("pendiente"), pendiente);
        };
    }

    // Especificación para filtrar por empleado_id
    public static Specification<Evaluacion> hasEmpleadoId(Integer empleadoId) {
        return (root, query, criteriaBuilder) -> {
            if (empleadoId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("empleado_id"), empleadoId);
        };
    }
}
