package com.testeapi.cadastrar_Veiculos.domain;

import java.util.Arrays;

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

    Marca(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public static Marca converterDeTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("A marca não pode ser vazia.");
        }

        return Arrays.stream(Marca.values())
                .filter(m -> m.name().equalsIgnoreCase(texto.trim())
                        || m.getNomeExibicao().equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Marca inválida: '" + texto + "'. Marcas aceitas: " + Arrays.toString(Marca.values())
                ));
    }
}