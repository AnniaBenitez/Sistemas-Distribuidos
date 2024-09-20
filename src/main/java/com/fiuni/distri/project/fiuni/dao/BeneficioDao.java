package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficioDao extends JpaRepository<Beneficio, Integer>, JpaSpecificationExecutor<Beneficio> {
}
