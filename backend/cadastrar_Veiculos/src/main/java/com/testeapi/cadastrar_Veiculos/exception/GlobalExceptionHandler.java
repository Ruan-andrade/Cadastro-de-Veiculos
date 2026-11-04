package com.testeapi.cadastrar_Veiculos.exception;


import com.testeapi.cadastrar_Veiculos.dto.ErroPadraoDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Captura quando o utilizador digita marca incorreta (ex: "Xevrolé")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDTO> tratarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Requisição Inválida")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 2. Captura quando um ID de veículo não existe na base de dados
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> tratarNaoEncontrado(ResourceNotFoundException ex, HttpServletRequest request) {
        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Não Encontrado")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 3. Captura campos vazios validados pelas anotações (@NotBlank, @NotNull)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> tratarValidacoes(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String primeiraMensagem = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Validação de Campos")
                .mensagem(primeiraMensagem)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

}
