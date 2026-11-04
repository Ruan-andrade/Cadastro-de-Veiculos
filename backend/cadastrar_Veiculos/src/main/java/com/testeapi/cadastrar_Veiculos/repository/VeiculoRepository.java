package com.testeapi.cadastrar_Veiculos.repository;

import com.testeapi.cadastrar_Veiculos.domain.Marca;
import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    // 1. Quantidade de veículos não vendidos
    long countByVendidoFalse();

    // 2. Veículos cadastrados na última semana
    List<Veiculo> findByCreatedGreaterThanEqual(LocalDateTime dataLimite);
    // 3. Filtros simples suportados nativamente pelo Spring Data
    List<Veiculo> findByMarca(Marca marca);
    List<Veiculo> findByAno(Integer ano);
    List<Veiculo> findByMarcaAndAno(Marca marca, Integer ano);
}