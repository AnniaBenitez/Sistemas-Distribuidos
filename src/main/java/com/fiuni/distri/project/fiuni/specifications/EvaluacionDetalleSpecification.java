package com.fiuni.distri.project.fiuni.specifications;

import com.fiuni.distri.project.fiuni.domain.EvaluacionDetalle;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionDetalleSpecification {

    // Filtrar por evaluacion_id
    public static Specification<EvaluacionDetalle> hasEvaluacionId(Integer evaluacionId) {
        return (root, query, criteriaBuilder) -> {
            if (evaluacionId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("evaluacion").get("id"), evaluacionId); // Ajustado para acceder a la entidad relacionada
        };
    }

    // Filtrar por criterio
    public static Specification<EvaluacionDetalle> hasCriterio(String criterio) {
        return (root, query, criteriaBuilder) -> {
            if (criterio == null || criterio.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("criterio")), "%" + criterio.toLowerCase() + "%");
        };
    }

    // Filtrar por puntaje (ajustado a Integer en vez de BigDecimal)
    public static Specification<EvaluacionDetalle> hasPuntaje(Integer puntaje) {
        return (root, query, criteriaBuilder) -> {
            if (puntaje == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("puntaje"), puntaje);
        };
    }

    // Filtrar por comentarios
    public static Specification<EvaluacionDetalle> hasComentarios(String comentarios) {
        return (root, query, criteriaBuilder) -> {
            if (comentarios == null || comentarios.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("comentarios")), "%" + comentarios.toLowerCase() + "%");
        };
    }
}
