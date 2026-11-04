package com.testeapi.cadastrar_Veiculos.service;
import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;

import java.util.List;

    public interface VeiculoService {

        VeiculoResponseDTO criar(VeiculoRequestDTO request);

        List<VeiculoResponseDTO> listarTodos(String marca, Integer ano);

        VeiculoResponseDTO buscarPorId(Long id);

        VeiculoResponseDTO atualizarCompleto(Long id, VeiculoRequestDTO request);

        VeiculoResponseDTO atualizarParcial(Long id, VeiculoPatchDTO patch);

        void excluir(Long id);

        RelatorioEstatisticasDTO gerarRelatorioEstatisticas();

        List<VeiculoResponseDTO> listarUltimaSemana();
    }

