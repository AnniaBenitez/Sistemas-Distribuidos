package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.BeneficioDetalle;
import com.fiuni.distri.project.fiuni.domain.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionDao extends JpaRepository<Evaluacion, Integer>, JpaSpecificationExecutor<Evaluacion> {
}
