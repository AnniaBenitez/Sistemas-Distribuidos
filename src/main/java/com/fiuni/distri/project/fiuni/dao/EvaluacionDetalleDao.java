package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.domain.EvaluacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionDetalleDao extends JpaRepository<EvaluacionDetalle, Integer>, JpaSpecificationExecutor<EvaluacionDetalle> {
}
