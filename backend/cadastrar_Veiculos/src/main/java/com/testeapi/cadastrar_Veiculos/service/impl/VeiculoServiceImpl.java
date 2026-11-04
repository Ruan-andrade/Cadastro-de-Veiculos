package com.testeapi.cadastrar_Veiculos.service.impl;

import com.testeapi.cadastrar_Veiculos.domain.Marca;
import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.exception.ResourceNotFoundException;
import com.testeapi.cadastrar_Veiculos.repository.VeiculoRepository;
import com.testeapi.cadastrar_Veiculos.service.VeiculoService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO request) {
        Marca marcaValida = Marca.converterDeTexto(request.getMarca());

        Veiculo veiculo =
                Veiculo.builder()
                        .veiculo(request.getVeiculo())
                        .marca(marcaValida)
                        .ano(request.getAno())
                        .cor(request.getCor())
                        .descricao(request.getDescricao())
                        .vendido(request.getVendido() != null && request.getVendido())
                        .build();

        return VeiculoResponseDTO.fromEntity(veiculoRepository.save(veiculo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarTodos(String marcaTexto, Integer ano, String cor) {
        Specification<Veiculo> spec = criarFiltro(marcaTexto, ano, cor);
        List<Veiculo> veiculos = veiculoRepository.findAll(spec);

        return veiculos.stream().map(VeiculoResponseDTO::fromEntity).collect(Collectors.toList());
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
        veiculo.setCor(request.getCor());
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
        if (patch.getCor() != null && !patch.getCor().isBlank()) {
            veiculo.setCor(patch.getCor());
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

        long naoVendidos = todos.stream().filter(v -> Boolean.FALSE.equals(v.getVendido())).count();

        // Distribuição por década (ex: 1995 -> Década 1990)
        Map<String, Long> decadas =
                todos.stream()
                        .filter(v -> v.getAno() != null)
                        .collect(
                                Collectors.groupingBy(
                                        v -> "Década " + ((v.getAno() / 10) * 10),
                                        LinkedHashMap::new,
                                        Collectors.counting()));

        // Distribuição por fabricante
        Map<String, Long> fabricantes =
                todos.stream()
                        .filter(v -> v.getMarca() != null)
                        .collect(
                                Collectors.groupingBy(
                                        v -> v.getMarca().getNomeExibicao(),
                                        LinkedHashMap::new,
                                        Collectors.counting()));

        LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
        long totalUltimaSemana =
                todos.stream()
                        .filter(
                                v ->
                                        v.getCreated() != null
                                                && (v.getCreated().isEqual(seteDiasAtras)
                                                        || v.getCreated().isAfter(seteDiasAtras)))
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
    public List<VeiculoResponseDTO> listarCarrosUltimaSemana() {
        LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
        return veiculoRepository.findByCreatedGreaterThanEqual(seteDiasAtras).stream()
                .map(VeiculoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Veiculo buscarEntidadePorId(Long id) {
        return veiculoRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Veículo não encontrado com o ID: " + id));
    }

    private Specification<Veiculo> criarFiltro(String marcaTexto, Integer ano, String cor) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (marcaTexto != null && !marcaTexto.isBlank()) {
                Marca marca = Marca.converterDeTexto(marcaTexto);
                predicates.add(cb.equal(root.get("marca"), marca));
            }
            if (ano != null) {
                predicates.add(cb.equal(root.get("ano"), ano));
            }
            if (cor != null && !cor.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("cor")), cor.trim().toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
