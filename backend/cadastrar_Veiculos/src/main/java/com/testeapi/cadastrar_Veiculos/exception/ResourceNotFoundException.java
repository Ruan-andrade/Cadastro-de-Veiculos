package com.testeapi.cadastrar_Veiculos.exception;

public class ResourceNotFoundException extends RuntimeException{

        public ResourceNotFoundException (String mensagem) {
            super(mensagem);
        }

}