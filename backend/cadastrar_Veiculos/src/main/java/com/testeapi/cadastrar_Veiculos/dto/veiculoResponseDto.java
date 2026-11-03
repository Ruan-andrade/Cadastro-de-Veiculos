package com.testeapi.cadastrar_Veiculos.dto;

import lombok.*;

public class veiculoResponseDto {
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
        private String descricao;
        private Boolean vendido;
        private LocalDateTime created;
        private LocalDateTime updated;

        // Método utilitário para converter da Entidade de domínio para o DTO
        public static VeiculoResponseDTO fromEntity(Veiculo entidade) {
            return VeiculoResponseDTO.builder()
                    .id(entidade.getId())
                    .veiculo(entidade.getVeiculo())
                    .marca(entidade.getMarca().getNomeExibicao())
                    .ano(entidade.getAno())
                    .descricao(entidade.getDescricao())
                    .vendido(entidade.getVendido())
                    .created(entidade.getCreated())
                    .updated(entidade.getUpdated())
                    .build();
        }
    }
}
