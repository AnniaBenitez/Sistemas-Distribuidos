package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



    @Repository
    public interface EmpleadoDao extends JpaRepository<Empleado, Integer> {
    }


