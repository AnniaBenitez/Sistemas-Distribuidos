package com.fiuni.distri.project.fiuni.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficioDetalleDao extends JpaRepository<BeneficioDetalle, Integer>, JpaSpecificationExecutor<BeneficioDetalle> {
}
