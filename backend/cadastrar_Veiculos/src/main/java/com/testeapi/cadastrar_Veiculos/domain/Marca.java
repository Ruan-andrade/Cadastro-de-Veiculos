package com.testeapi.cadastrar_Veiculos.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Marca {
    CHEVROLET("Chevrolet"),
    VOLKSWAGEN("Volkswagen"),
    FIAT("Fiat"),
    FORD("Ford"),
    TOYOTA("Toyota"),
    HONDA("Honda"),
    HYUNDAI("Hyundai"),
    RENAULT("Renault"),
    NISSAN("Nissan"),
    JEEP("Jeep");

    private final String nomeExibicao;

    public static Marca converterDeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("A marca não pode ser vazia.");
        }

        return Arrays.stream(Marca.values())
                .filter(
                        m ->
                                m.name().equalsIgnoreCase(texto.trim())
                                        || m.getNomeExibicao().equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Marca inválida: '"
                                                + texto
                                                + "'. Marcas aceitas: "
                                                + Arrays.toString(Marca.values())));
    }
}
