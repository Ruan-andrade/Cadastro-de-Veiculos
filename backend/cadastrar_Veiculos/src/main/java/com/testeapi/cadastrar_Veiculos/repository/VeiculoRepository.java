package com.testeapi.cadastrar_Veiculos.repository;

import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository
        extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {

    // 1. Quantidade de veículos não vendidos
    long countByVendidoFalse();

    // 2. Veículos cadastrados na última semana
    List<Veiculo> findByCreatedGreaterThanEqual(LocalDateTime dataLimite);
}
