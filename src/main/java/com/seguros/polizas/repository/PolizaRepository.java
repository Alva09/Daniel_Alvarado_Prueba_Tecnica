package com.seguros.polizas.repository;

import com.seguros.polizas.domain.entity.Poliza;
import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);
}
