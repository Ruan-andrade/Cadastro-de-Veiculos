package com.testeapi.cadastrar_Veiculos.controller;

import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite a comunicação com a SPA no Front-end sem bloqueio de CORS
public class VeiculoController {

    private final VeiculoService veiculoService;

    // 1. POST /veiculos -> Adiciona um novo veículo
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> cadastrar(@RequestBody @Valid VeiculoRequestDTO request) {
        VeiculoResponseDTO response = veiculoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. GET /veiculos ou GET /veiculos?marca={marca}&ano={ano} -> Lista e filtra veículos
    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> listar(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer ano) {
        List<VeiculoResponseDTO> veiculos = veiculoService.listarTodos(marca, ano);
        return ResponseEntity.ok(veiculos);
    }

    // 3. GET /veiculos/{id} -> Retorna os detalhes de um veículo
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorId(@PathVariable Long id) {
        VeiculoResponseDTO veiculo = veiculoService.buscarPorId(id);
        return ResponseEntity.ok(veiculo);
    }

    // 4. PUT /veiculos/{id} -> Atualiza todos os dados de um veículo
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizarCompleto(
            @PathVariable Long id,
            @RequestBody @Valid VeiculoRequestDTO request) {
        VeiculoResponseDTO atualizado = veiculoService.atualizarCompleto(id, request);
        return ResponseEntity.ok(atualizado);
    }

    // 5. PATCH /veiculos/{id} -> Atualiza apenas alguns dados de um veículo
    @PatchMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizarParcial(
            @PathVariable Long id,
            @RequestBody VeiculoPatchDTO patch) {
        VeiculoResponseDTO atualizado = veiculoService.atualizarParcial(id, patch);
        return ResponseEntity.ok(atualizado);
    }

    // 6. DELETE /veiculos/{id} -> Apaga o veículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        veiculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 7. GET /veiculos/relatorios -> Retorna dados consolidados (não vendidos, décadas, fabricantes)
    @GetMapping("/relatorios")
    public ResponseEntity<RelatorioEstatisticasDTO> relatorios() {
        RelatorioEstatisticasDTO estatisticas = veiculoService.gerarRelatorioEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }

    // 8. GET /veiculos/semana -> Retorna carros registados na última semana
    @GetMapping("/semana")
    public ResponseEntity<Long> listarSemana() {
        long recentes = veiculoService.listarUltimaSemana();
        return ResponseEntity.ok(recentes);
    }

}
