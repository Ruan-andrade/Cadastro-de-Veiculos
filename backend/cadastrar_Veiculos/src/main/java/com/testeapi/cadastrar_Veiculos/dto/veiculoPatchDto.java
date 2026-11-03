package com.testeapi.cadastrar_Veiculos.dto;
import lombok.*;
public class veiculoPatchDto {



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class VeiculoPatchDTO {

        private String veiculo;
        private String marca;
        private Integer ano;
        private String descricao;
        private Boolean vendido;
    }
}
