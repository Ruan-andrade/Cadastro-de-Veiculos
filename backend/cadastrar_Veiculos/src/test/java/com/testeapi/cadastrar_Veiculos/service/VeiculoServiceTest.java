package com.testeapi.cadastrar_Veiculos.service;

import com.testeapi.cadastrar_Veiculos.domain.Marca;
import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.repository.VeiculoRepository;
import com.testeapi.cadastrar_Veiculos.service.impl.VeiculoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private VeiculoServiceImpl service;

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarVeiculoTestes {

        @Test
        @DisplayName("Deve salvar veículo com sucesso quando dados e marca forem válidos")
        void deveSalvarVeiculoQuandoDadosValidos() {
            VeiculoRequestDTO request = VeiculoRequestDTO.builder()
                    .veiculo("Civic Touring")
                    .marca("Honda")
                    .ano(2021)
                    .descricao("Sedan em ótimo estado")
                    .vendido(false)
                    .build();

            Veiculo veiculoSalvo = Veiculo.builder()
                    .id(1L)
                    .veiculo("Civic Touring")
                    .marca(Marca.HONDA)
                    .ano(2021)
                    .descricao("Sedan em ótimo estado")
                    .vendido(false)
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .build();

            when(repository.save(any(Veiculo.class))).thenReturn(veiculoSalvo);

            VeiculoResponseDTO resultado = service.criar(request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getMarca()).isEqualTo("Honda");
            assertThat(resultado.getVeiculo()).isEqualTo("Civic Touring");
            verify(repository, times(1)).save(any(Veiculo.class));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando marca for inválida")
        void deveLancarExcecaoQuandoMarcaInvalida() {
            VeiculoRequestDTO request = VeiculoRequestDTO.builder()
                    .veiculo("Corsa")
                    .marca("Xevrole")
                    .ano(2005)
                    .descricao("Teste de falha")
                    .vendido(false)
                    .build();

            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(repository, never()).save(any(Veiculo.class));
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (GET)")
    class BuscarPorIdTestes {

        @Test
        @DisplayName("Deve retornar o veículo quando o ID existir")
        void deveRetornarVeiculoQuandoIdExistir() {
            Veiculo veiculo = Veiculo.builder()
                    .id(10L)
                    .veiculo("Corolla")
                    .marca(Marca.TOYOTA)
                    .ano(2020)
                    .descricao("Conservado")
                    .vendido(false)
                    .build();

            when(repository.findById(10L)).thenReturn(Optional.of(veiculo));

            VeiculoResponseDTO response = service.buscarPorId(10L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(10L);
            assertThat(response.getMarca()).isEqualTo("Toyota");
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Veículo não encontrado com o ID: 99");
        }
    }

    @Nested
    @DisplayName("Cenários de Relatórios e Estatísticas")
    class RelatoriosTestes {

        @Test
        @DisplayName("Deve calcular corretamente os veículos não vendidos e distribuição por década")
        void deveCalcularRelatorioComPrecisao() {
            Veiculo v1 = Veiculo.builder()
                    .id(1L)
                    .veiculo("Gol")
                    .marca(Marca.VOLKSWAGEN)
                    .ano(1994)
                    .vendido(false)
                    .created(LocalDateTime.now())
                    .build();

            Veiculo v2 = Veiculo.builder()
                    .id(2L)
                    .veiculo("Ka")
                    .marca(Marca.FORD)
                    .ano(1998)
                    .vendido(true)
                    .created(LocalDateTime.now())
                    .build();

            Veiculo v3 = Veiculo.builder()
                    .id(3L)
                    .veiculo("Polo")
                    .marca(Marca.VOLKSWAGEN)
                    .ano(2022)
                    .vendido(false)
                    .created(LocalDateTime.now())
                    .build();

            // O seu método gerarRelatorioEstatisticas() chama apenas findAll()
            when(repository.findAll()).thenReturn(List.of(v1, v2, v3));

            RelatorioEstatisticasDTO relatorio = service.gerarRelatorioEstatisticas();

            assertThat(relatorio).isNotNull();
            assertThat(relatorio.getTotalNaoVendidos()).isEqualTo(2L);
            assertThat(relatorio.getTotalUltimaSemana()).isEqualTo(3L);
            assertThat(relatorio.getDistribuicaoPorDecada())
                    .containsEntry("Década 1990", 2L)
                    .containsEntry("Década 2020", 1L);
            assertThat(relatorio.getDistribuicaoPorFabricante())
                    .containsEntry("Volkswagen", 2L)
                    .containsEntry("Ford", 1L);
        }
    }
}