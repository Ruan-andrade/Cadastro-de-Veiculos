package com.testeapi.cadastrar_Veiculos.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class VeiculoRequestDTO {

        @NotBlank(message = "O nome do veículo é obrigatório.")
        private String veiculo;

        @NotBlank(message = "A marca é obrigatória.")
        private String marca;

        @NotNull(message = "O ano é obrigatório.")
        @Positive(message = "O ano deve ser um número positivo.")
        private Integer ano;

        private String descricao;

        private Boolean vendido;
    }
}
