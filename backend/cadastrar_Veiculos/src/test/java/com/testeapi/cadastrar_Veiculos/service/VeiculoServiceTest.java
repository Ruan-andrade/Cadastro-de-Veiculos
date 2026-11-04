package com.testeapi.cadastrar_Veiculos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.testeapi.cadastrar_Veiculos.domain.Marca;
import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import com.testeapi.cadastrar_Veiculos.dto.RelatorioEstatisticasDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoPatchDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoRequestDTO;
import com.testeapi.cadastrar_Veiculos.dto.VeiculoResponseDTO;
import com.testeapi.cadastrar_Veiculos.exception.ResourceNotFoundException;
import com.testeapi.cadastrar_Veiculos.repository.VeiculoRepository;
import com.testeapi.cadastrar_Veiculos.service.impl.VeiculoServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock private VeiculoRepository repository;

    @InjectMocks private VeiculoServiceImpl service;

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarVeiculoTestes {

        @Test
        @DisplayName("Deve salvar veículo com sucesso quando dados e marca forem válidos")
        void deveSalvarVeiculoQuandoDadosValidos() {
            VeiculoRequestDTO request =
                    VeiculoRequestDTO.builder()
                            .veiculo("Civic Touring")
                            .marca("Honda")
                            .ano(2021)
                            .cor("Preto")
                            .descricao("Sedan em ótimo estado")
                            .vendido(false)
                            .build();

            Veiculo veiculoSalvo =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Civic Touring")
                            .marca(Marca.HONDA)
                            .ano(2021)
                            .cor("Preto")
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
            assertThat(resultado.getCor()).isEqualTo("Preto");
            verify(repository, times(1)).save(any(Veiculo.class));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando marca for inválida")
        void deveLancarExcecaoQuandoMarcaInvalida() {
            VeiculoRequestDTO request =
                    VeiculoRequestDTO.builder()
                            .veiculo("Corsa")
                            .marca("Xevrole")
                            .ano(2005)
                            .descricao("Teste de falha")
                            .vendido(false)
                            .build();

            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Marca inválida");

            verify(repository, never()).save(any(Veiculo.class));
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (GET)")
    class BuscarPorIdTestes {

        @Test
        @DisplayName("Deve retornar o veículo quando o ID existir")
        void deveRetornarVeiculoQuandoIdExistir() {
            Veiculo veiculo =
                    Veiculo.builder()
                            .id(10L)
                            .veiculo("Corolla")
                            .marca(Marca.TOYOTA)
                            .ano(2020)
                            .cor("Prata")
                            .descricao("Conservado")
                            .vendido(false)
                            .build();

            when(repository.findById(10L)).thenReturn(Optional.of(veiculo));

            VeiculoResponseDTO response = service.buscarPorId(10L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(10L);
            assertThat(response.getMarca()).isEqualTo("Toyota");
            assertThat(response.getCor()).isEqualTo("Prata");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Veículo não encontrado com o ID: 99");
        }
    }

    @Nested
    @DisplayName("Cenários de Listagem e Filtro (GET)")
    class ListarTodosTestes {

        @Test
        @DisplayName("Deve listar todos os veículos usando Specification")
        @SuppressWarnings("unchecked")
        void deveListarTodosOsVeiculos() {
            Veiculo v =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Gol")
                            .marca(Marca.VOLKSWAGEN)
                            .ano(2015)
                            .cor("Branco")
                            .vendido(false)
                            .build();

            when(repository.findAll(any(Specification.class))).thenReturn(List.of(v));

            List<VeiculoResponseDTO> resultado = service.listarTodos(null, null, null);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getVeiculo()).isEqualTo("Gol");
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização Completa (PUT)")
    class AtualizarCompletoTestes {

        @Test
        @DisplayName("Deve atualizar veículo completo quando ID for válido")
        void deveAtualizarVeiculoCompleto() {
            Veiculo existente =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Uno")
                            .marca(Marca.FIAT)
                            .ano(2010)
                            .cor("Vermelho")
                            .vendido(false)
                            .build();

            VeiculoRequestDTO request =
                    VeiculoRequestDTO.builder()
                            .veiculo("Uno Way")
                            .marca("Fiat")
                            .ano(2011)
                            .cor("Preto")
                            .descricao("Revisado")
                            .vendido(true)
                            .build();

            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(repository.save(any(Veiculo.class))).thenAnswer(i -> i.getArgument(0));

            VeiculoResponseDTO atualizado = service.atualizarCompleto(1L, request);

            assertThat(atualizado.getVeiculo()).isEqualTo("Uno Way");
            assertThat(atualizado.getAno()).isEqualTo(2011);
            assertThat(atualizado.getCor()).isEqualTo("Preto");
            assertThat(atualizado.getVendido()).isTrue();
        }

        @Test
        @DisplayName(
                "Deve lançar ResourceNotFoundException ao tentar atualizar veículo inexistente")
        void deveFalharAoAtualizarInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            VeiculoRequestDTO request =
                    VeiculoRequestDTO.builder().veiculo("Uno").marca("Fiat").ano(2010).build();

            assertThatThrownBy(() -> service.atualizarCompleto(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização Parcial (PATCH)")
    class AtualizarParcialTestes {

        @Test
        @DisplayName("Deve atualizar apenas campos enviados no PATCH")
        void deveAtualizarApenasCamposInformados() {
            Veiculo existente =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Compass")
                            .marca(Marca.JEEP)
                            .ano(2020)
                            .cor("Branco")
                            .vendido(false)
                            .build();

            VeiculoPatchDTO patch = VeiculoPatchDTO.builder().vendido(true).cor("Azul").build();

            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(repository.save(any(Veiculo.class))).thenAnswer(i -> i.getArgument(0));

            VeiculoResponseDTO atualizado = service.atualizarParcial(1L, patch);

            assertThat(atualizado.getVendido()).isTrue();
            assertThat(atualizado.getCor()).isEqualTo("Azul");
            assertThat(atualizado.getVeiculo()).isEqualTo("Compass"); // Não alterado
            assertThat(atualizado.getAno()).isEqualTo(2020); // Não alterado
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException em PATCH de ID inexistente")
        void deveFalharPatchInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            VeiculoPatchDTO patch = VeiculoPatchDTO.builder().vendido(true).build();

            assertThatThrownBy(() -> service.atualizarParcial(99L, patch))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (DELETE)")
    class ExcluirTestes {

        @Test
        @DisplayName("Deve excluir veículo existente")
        void deveExcluirVeiculo() {
            Veiculo existente = Veiculo.builder().id(1L).build();
            when(repository.findById(1L)).thenReturn(Optional.of(existente));

            service.excluir(1L);

            verify(repository, times(1)).delete(existente);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao excluir veículo inexistente")
        void deveFalharAoExcluirInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.excluir(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).delete(any(Veiculo.class));
        }
    }

    @Nested
    @DisplayName("Cenários de Relatórios e Estatísticas")
    class RelatoriosTestes {

        @Test
        @DisplayName(
                "Deve calcular corretamente os veículos não vendidos e distribuição por década e fabricante")
        void deveCalcularRelatorioComPrecisao() {
            Veiculo v1 =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Gol")
                            .marca(Marca.VOLKSWAGEN)
                            .ano(1994)
                            .vendido(false)
                            .created(LocalDateTime.now())
                            .build();

            Veiculo v2 =
                    Veiculo.builder()
                            .id(2L)
                            .veiculo("Ka")
                            .marca(Marca.FORD)
                            .ano(1998)
                            .vendido(true)
                            .created(LocalDateTime.now())
                            .build();

            Veiculo v3 =
                    Veiculo.builder()
                            .id(3L)
                            .veiculo("Polo")
                            .marca(Marca.VOLKSWAGEN)
                            .ano(2022)
                            .vendido(false)
                            .created(LocalDateTime.now())
                            .build();

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

        @Test
        @DisplayName("Deve retornar a lista de carros registrados durante a última semana")
        void deveListarCarrosUltimaSemana() {
            Veiculo v =
                    Veiculo.builder()
                            .id(1L)
                            .veiculo("Clio")
                            .marca(Marca.RENAULT)
                            .ano(2018)
                            .created(LocalDateTime.now().minusDays(2))
                            .build();

            when(repository.findByCreatedGreaterThanEqual(any(LocalDateTime.class)))
                    .thenReturn(List.of(v));

            List<VeiculoResponseDTO> resultado = service.listarCarrosUltimaSemana();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getVeiculo()).isEqualTo("Clio");
            assertThat(resultado.get(0).getMarca()).isEqualTo("Renault");
        }
    }
}
