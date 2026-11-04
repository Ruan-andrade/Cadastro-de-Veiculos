package com.testeapi.cadastrar_Veiculos.dto;

import com.testeapi.cadastrar_Veiculos.domain.Veiculo;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoResponseDTO {

    private Long id;
    private String veiculo;
    private String marca;
    private Integer ano;
    private String cor;
    private String descricao;
    private Boolean vendido;
    private LocalDateTime created;
    private LocalDateTime updated;

    public static VeiculoResponseDTO fromEntity(Veiculo entidade) {
        return VeiculoResponseDTO.builder()
                .id(entidade.getId())
                .veiculo(entidade.getVeiculo())
                .marca(entidade.getMarca() != null ? entidade.getMarca().getNomeExibicao() : null)
                .ano(entidade.getAno())
                .cor(entidade.getCor())
                .descricao(entidade.getDescricao())
                .vendido(entidade.getVendido())
                .created(entidade.getCreated())
                .updated(entidade.getUpdated())
                .build();
    }
}
