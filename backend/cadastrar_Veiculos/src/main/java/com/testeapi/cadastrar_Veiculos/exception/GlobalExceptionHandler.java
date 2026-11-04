package com.testeapi.cadastrar_Veiculos.exception;

import com.testeapi.cadastrar_Veiculos.dto.ErroPadraoDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Marca inválida ou argumento ilegal (ex: "Xevrolé")
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

    // 2. ID não encontrado no banco
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

    // 3. Validação de DTOs (@NotBlank, @NotNull) - junta todos os erros caso haja mais de um
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> tratarValidacoes(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagens = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (mensagens.isBlank()) {
            mensagens = "Dados inválidos enviados na requisição.";
        }

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Validação de Campos")
                .mensagem(mensagens)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 4. JSON malformado ou tipos incompatíveis (ex: texto no campo de ano inteiro)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroPadraoDTO> tratarLeituraJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("JSON Inválido")
                .mensagem("O corpo da requisição contém tipos de dados inválidos ou formatação JSON incorreta.")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}