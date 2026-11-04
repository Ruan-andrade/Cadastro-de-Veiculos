package com.testeapi.cadastrar_Veiculos.dto;

import java.util.Map;
import lombok.*;

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
