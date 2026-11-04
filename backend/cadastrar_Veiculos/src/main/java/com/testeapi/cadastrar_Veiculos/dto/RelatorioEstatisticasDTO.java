package com.testeapi.cadastrar_Veiculos.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioEstatisticasDTO {

    private long totalNaoVendidos;
    private Map<String, Long> distribuicaoPorDecada;
    private Map<String, Long> distribuicaoPorFabricante;
    private long totalUltimaSemana;
}

