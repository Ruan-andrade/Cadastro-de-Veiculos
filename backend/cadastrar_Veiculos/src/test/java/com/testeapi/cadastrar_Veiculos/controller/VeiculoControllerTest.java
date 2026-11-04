package com.testeapi.cadastrar_Veiculos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.exception.GlobalExceptionHandler;
import com.testeapi.cadastrar_Veiculos.exception.ResourceNotFoundException;
import com.testeapi.cadastrar_Veiculos.service.VeiculoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    private MockMvc mockMvc;

    @Mock private VeiculoService veiculoService;

    @InjectMocks private VeiculoController veiculoController;

    @BeforeEach
    void setUp() {
        this.mockMvc =
                MockMvcBuilders.standaloneSetup(veiculoController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    @DisplayName("POST /veiculos - Deve cadastrar veículo e retornar 201 Created")
    void deveCadastrarVeiculoComSucesso() throws Exception {
        VeiculoResponseDTO response =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Civic Touring")
                        .marca("Honda")
                        .ano(2021)
                        .cor("Preto")
                        .descricao("Impecável")
                        .vendido(false)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build();

        when(veiculoService.criar(any(VeiculoRequestDTO.class))).thenReturn(response);

        String payloadJson =
                """
                {
                    "veiculo": "Civic Touring",
                    "marca": "Honda",
                    "ano": 2021,
                    "cor": "Preto",
                    "descricao": "Impecável",
                    "vendido": false
                }
                """;

        mockMvc.perform(
                        post("/veiculos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.veiculo").value("Civic Touring"))
                .andExpect(jsonPath("$.marca").value("Honda"))
                .andExpect(jsonPath("$.cor").value("Preto"));
    }

    @Test
    @DisplayName("POST /veiculos - Deve retornar 400 Bad Request ao omitir campos obrigatórios")
    void deveRetornar400AoCadastrarInvalido() throws Exception {
        String payloadInvalido =
                """
                {
                    "veiculo": "",
                    "ano": -10
                }
                """;

        mockMvc.perform(
                        post("/veiculos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Validação de Campos"));
    }

    @Test
    @DisplayName("POST /veiculos - Deve retornar 400 Bad Request quando a marca for inválida")
    void deveRetornar400QuandoMarcaInvalida() throws Exception {
        when(veiculoService.criar(any(VeiculoRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Marca inválida: 'Xevrole'."));

        String payloadMarcaInvalida =
                """
                {
                    "veiculo": "Corsa",
                    "marca": "Xevrole",
                    "ano": 2005
                }
                """;

        mockMvc.perform(
                        post("/veiculos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadMarcaInvalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Requisição Inválida"))
                .andExpect(jsonPath("$.mensagem").value("Marca inválida: 'Xevrole'."));
    }

    @Test
    @DisplayName("GET /veiculos - Deve listar veículos e retornar 200 OK")
    void deveListarVeiculos() throws Exception {
        VeiculoResponseDTO v =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Corolla")
                        .marca("Toyota")
                        .ano(2022)
                        .cor("Branco")
                        .vendido(false)
                        .build();

        when(veiculoService.listarTodos(null, null, null)).thenReturn(List.of(v));

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].veiculo").value("Corolla"));
    }

    @Test
    @DisplayName(
            "GET /veiculos?marca=Toyota&ano=2022&cor=Branco - Deve filtrar veículos e retornar 200 OK")
    void deveFiltrarVeiculos() throws Exception {
        VeiculoResponseDTO v =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Corolla")
                        .marca("Toyota")
                        .ano(2022)
                        .cor("Branco")
                        .vendido(false)
                        .build();

        when(veiculoService.listarTodos("Toyota", 2022, "Branco")).thenReturn(List.of(v));

        mockMvc.perform(
                        get("/veiculos")
                                .param("marca", "Toyota")
                                .param("ano", "2022")
                                .param("cor", "Branco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].marca").value("Toyota"));
    }

    @Test
    @DisplayName("GET /veiculos/{id} - Deve retornar 200 OK quando o veículo existir")
    void deveBuscarPorIdComSucesso() throws Exception {
        VeiculoResponseDTO v =
                VeiculoResponseDTO.builder()
                        .id(5L)
                        .veiculo("Compass")
                        .marca("Jeep")
                        .ano(2023)
                        .build();

        when(veiculoService.buscarPorId(5L)).thenReturn(v);

        mockMvc.perform(get("/veiculos/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.veiculo").value("Compass"));
    }

    @Test
    @DisplayName("GET /veiculos/{id} - Deve retornar 404 Not Found quando veículo não existir")
    void deveRetornar404QuandoVeiculoNaoExistir() throws Exception {
        when(veiculoService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Veículo não encontrado com o ID: 99"));

        mockMvc.perform(get("/veiculos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Não Encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Veículo não encontrado com o ID: 99"));
    }

    @Test
    @DisplayName("PUT /veiculos/{id} - Deve atualizar veículo completamente e retornar 200 OK")
    void deveAtualizarVeiculoCompleto() throws Exception {
        VeiculoResponseDTO response =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Gol")
                        .marca("Volkswagen")
                        .ano(2010)
                        .cor("Vermelho")
                        .vendido(true)
                        .build();

        when(veiculoService.atualizarCompleto(eq(1L), any(VeiculoRequestDTO.class)))
                .thenReturn(response);

        String payloadPut =
                """
                {
                    "veiculo": "Gol",
                    "marca": "Volkswagen",
                    "ano": 2010,
                    "cor": "Vermelho",
                    "vendido": true
                }
                """;

        mockMvc.perform(
                        put("/veiculos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadPut))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vendido").value(true));
    }

    @Test
    @DisplayName("PUT /veiculos/{id} - Deve retornar 404 Not Found quando ID não existir")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        when(veiculoService.atualizarCompleto(eq(99L), any(VeiculoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Veículo não encontrado com o ID: 99"));

        String payloadPut =
                """
                {
                    "veiculo": "Gol",
                    "marca": "Volkswagen",
                    "ano": 2010
                }
                """;

        mockMvc.perform(
                        put("/veiculos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadPut))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PATCH /veiculos/{id} - Deve atualizar parcialmente e retornar 200 OK")
    void deveAtualizarParcial() throws Exception {
        VeiculoResponseDTO response =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Polo")
                        .marca("Volkswagen")
                        .ano(2023)
                        .vendido(true)
                        .build();

        when(veiculoService.atualizarParcial(eq(1L), any(VeiculoPatchDTO.class)))
                .thenReturn(response);

        String payloadPatch =
                """
                {
                    "vendido": true
                }
                """;

        mockMvc.perform(
                        patch("/veiculos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadPatch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendido").value(true));
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} - Deve excluir veículo e retornar 204 No Content")
    void deveExcluirVeiculo() throws Exception {
        doNothing().when(veiculoService).excluir(1L);

        mockMvc.perform(delete("/veiculos/1")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName(
            "DELETE /veiculos/{id} - Deve retornar 404 Not Found ao tentar excluir inexistente")
    void deveRetornar404AoExcluirInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Veículo não encontrado com o ID: 99"))
                .when(veiculoService)
                .excluir(99L);

        mockMvc.perform(delete("/veiculos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /veiculos/relatorios - Deve retornar estatísticas e 200 OK")
    void deveRetornarRelatorios() throws Exception {
        RelatorioEstatisticasDTO dto =
                RelatorioEstatisticasDTO.builder()
                        .totalNaoVendidos(15L)
                        .distribuicaoPorDecada(Map.of("Década 2000", 10L))
                        .distribuicaoPorFabricante(Map.of("Honda", 8L))
                        .totalUltimaSemana(4L)
                        .build();

        when(veiculoService.gerarRelatorioEstatisticas()).thenReturn(dto);

        mockMvc.perform(get("/veiculos/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNaoVendidos").value(15L))
                .andExpect(jsonPath("$.distribuicaoPorFabricante.Honda").value(8L));
    }

    @Test
    @DisplayName("GET /veiculos/semana - Deve retornar lista de carros da última semana")
    void deveRetornarCarrosSemana() throws Exception {
        VeiculoResponseDTO v =
                VeiculoResponseDTO.builder()
                        .id(1L)
                        .veiculo("Civic")
                        .marca("Honda")
                        .ano(2022)
                        .build();

        when(veiculoService.listarCarrosUltimaSemana()).thenReturn(List.of(v));

        mockMvc.perform(get("/veiculos/semana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].veiculo").value("Civic"));
    }
}
