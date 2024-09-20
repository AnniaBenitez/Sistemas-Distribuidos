package com.fiuni.distri.project.fiuni.specifications;

import com.fiuni.distri.project.fiuni.domain.Beneficio;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BeneficioSpecification {

    // Especificación para filtrar por nombre, con búsqueda parcial e insensible a mayúsculas
    public static Specification<Beneficio> hasNombre(String nombre) {
        return (root, query, criteriaBuilder) -> {
            if (nombre == null || nombre.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
        };
    }

    // Especificación para filtrar por porcentaje_de_sueldo
    public static Specification<Beneficio> hasPorcentajeDeSueldo(BigDecimal porcentajeDeSueldo) {
        return (root, query, criteriaBuilder) -> {
            if (porcentajeDeSueldo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("porcentaje_de_sueldo"), porcentajeDeSueldo);
        };
    }

    // Especificación para filtrar por estado activo
    public static Specification<Beneficio> isActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }

    // Especificación para filtrar por fecha de inicio
    public static Specification<Beneficio> hasFechaInicio(LocalDateTime fechaInicio) {
        return (root, query, criteriaBuilder) -> {
            if (fechaInicio == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("fecha_inicio"), fechaInicio);
        };
    }

    // Especificación para filtrar por fecha de fin
    public static Specification<Beneficio> hasFechaFin(LocalDateTime fechaFin) {
        return (root, query, criteriaBuilder) -> {
            if (fechaFin == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("fecha_fin"), fechaFin);
        };
    }
}
