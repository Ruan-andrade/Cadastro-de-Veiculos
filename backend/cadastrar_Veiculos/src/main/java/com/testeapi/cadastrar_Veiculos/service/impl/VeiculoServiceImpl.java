package com.testeapi.cadastrar_Veiculos.service.impl;

import com.testeapi.cadastrar_Veiculos.domain.Marca;
import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.repository.VeiculoRepository;
import com.testeapi.cadastrar_Veiculos.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO request) {
        Marca marcaValida = Marca.converterDeTexto(request.getMarca());

        Veiculo veiculo = Veiculo.builder()
                .veiculo(request.getVeiculo())
                .marca(marcaValida)
                .ano(request.getAno())
                .descricao(request.getDescricao())
                .vendido(request.getVendido() != null && request.getVendido())
                .build();

        return VeiculoResponseDTO.fromEntity(veiculoRepository.save(veiculo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarTodos(String marcaTexto, Integer ano) {
        List<Veiculo> lista;

        if (marcaTexto != null && !marcaTexto.isBlank() && ano != null) {
            Marca marca = Marca.converterDeTexto(marcaTexto);
            lista = veiculoRepository.findByMarcaAndAno(marca, ano);
        } else if (marcaTexto != null && !marcaTexto.isBlank()) {
            Marca marca = Marca.converterDeTexto(marcaTexto);
            lista = veiculoRepository.findByMarca(marca);
        } else if (ano != null) {
            lista = veiculoRepository.findByAno(ano);
        } else {
            lista = veiculoRepository.findAll();
        }

        return lista.stream()
                .map(VeiculoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo veiculo = buscarEntidadePorId(id);
        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    @Override
    @Transactional
    public VeiculoResponseDTO atualizarCompleto(Long id, VeiculoRequestDTO request) {
        Veiculo veiculo = buscarEntidadePorId(id);
        Marca marcaValida = Marca.converterDeTexto(request.getMarca());

        veiculo.setVeiculo(request.getVeiculo());
        veiculo.setMarca(marcaValida);
        veiculo.setAno(request.getAno());
        veiculo.setDescricao(request.getDescricao());
        veiculo.setVendido(request.getVendido() != null ? request.getVendido() : false);

        return VeiculoResponseDTO.fromEntity(veiculoRepository.save(veiculo));
    }

    @Override
    @Transactional
    public VeiculoResponseDTO atualizarParcial(Long id, VeiculoPatchDTO patch) {
        Veiculo veiculo = buscarEntidadePorId(id);

        if (patch.getVeiculo() != null && !patch.getVeiculo().isBlank()) {
            veiculo.setVeiculo(patch.getVeiculo());
        }
        if (patch.getMarca() != null && !patch.getMarca().isBlank()) {
            veiculo.setMarca(Marca.converterDeTexto(patch.getMarca()));
        }
        if (patch.getAno() != null) {
            veiculo.setAno(patch.getAno());
        }
        if (patch.getDescricao() != null) {
            veiculo.setDescricao(patch.getDescricao());
        }
        if (patch.getVendido() != null) {
            veiculo.setVendido(patch.getVendido());
        }

        return VeiculoResponseDTO.fromEntity(veiculoRepository.save(veiculo));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Veiculo veiculo = buscarEntidadePorId(id);
        veiculoRepository.delete(veiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioEstatisticasDTO gerarRelatorioEstatisticas() {
        List<Veiculo> todos = veiculoRepository.findAll();

        long naoVendidos = todos.stream().filter(v -> !v.getVendido()).count();

        // Distribuição por década (ex: 1995 -> Década 1990)
        Map<String, Long> decadas = todos.stream()
                .collect(Collectors.groupingBy(
                        v -> "Década " + ((v.getAno() / 10) * 10),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        // Distribuição por fabricante
        Map<String, Long> fabricantes = todos.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getMarca().getNomeExibicao(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
        long totalUltimaSemana = todos.stream()
                .filter(v -> v.getCreated().isAfter(seteDiasAtras))
                .count();

        return RelatorioEstatisticasDTO.builder()
                .totalNaoVendidos(naoVendidos)
                .distribuicaoPorDecada(decadas)
                .distribuicaoPorFabricante(fabricantes)
                .totalUltimaSemana(totalUltimaSemana)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarUltimaSemana() {
        LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
        return veiculoRepository.findByCreatedGreaterThanEqual(seteDiasAtras)
                .stream()
                .map(VeiculoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Veiculo buscarEntidadePorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com o ID: " + id));
    }
}

