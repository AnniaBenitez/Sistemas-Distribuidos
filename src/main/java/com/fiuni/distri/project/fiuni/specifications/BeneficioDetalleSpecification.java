package com.fiuni.distri.project.fiuni.specifications;

import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BeneficioDetalleSpecification {

    // Especificación para filtrar por beneficio_id
    public static Specification<BeneficioDetalle> hasBeneficioId(Integer beneficioId) {
        return (root, query, criteriaBuilder) -> {
            if (beneficioId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("beneficio_id"), beneficioId);
        };
    }

    // Especificación para filtrar por monto
    public static Specification<BeneficioDetalle> hasMonto(Double monto) {
        return (root, query, criteriaBuilder) -> {
            if (monto == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("monto"), monto);
        };
    }

    // Especificación para filtrar por empleado_id
    public static Specification<BeneficioDetalle> hasEmpleadoId(Integer empleadoId) {
        return (root, query, criteriaBuilder) -> {
            if (empleadoId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("empleado_id"), empleadoId);
        };
    }

    // Especificación para filtrar por estado activo
    public static Specification<BeneficioDetalle> isActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activo"), activo);
        };
    }
}