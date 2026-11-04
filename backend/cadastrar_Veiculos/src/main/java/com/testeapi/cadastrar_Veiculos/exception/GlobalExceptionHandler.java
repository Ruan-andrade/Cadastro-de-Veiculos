package com.testeapi.cadastrar_Veiculos.exception;

import com.testeapi.cadastrar_Veiculos.dto.ErroPadraoDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. ID ou recurso não encontrado no banco -> 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> tratarNaoEncontrado(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErroPadraoDTO erro =
                ErroPadraoDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .erro("Não Encontrado")
                        .mensagem(ex.getMessage())
                        .path(request.getRequestURI())
                        .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 2. Marca inválida ou argumento ilegal (ex: "Xevrolé") -> 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDTO> tratarArgumentoInvalido(
            IllegalArgumentException ex, HttpServletRequest request) {
        ErroPadraoDTO erro =
                ErroPadraoDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .erro("Requisição Inválida")
                        .mensagem(ex.getMessage())
                        .path(request.getRequestURI())
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 3. Validação de DTOs (@NotBlank, @NotNull, @Positive) -> 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> tratarValidacoes(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagens =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                fieldError ->
                                        fieldError.getField()
                                                + ": "
                                                + fieldError.getDefaultMessage())
                        .collect(Collectors.joining("; "));

        if (mensagens.isBlank()) {
            mensagens = "Dados inválidos enviados na requisição.";
        }

        ErroPadraoDTO erro =
                ErroPadraoDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .erro("Validação de Campos")
                        .mensagem(mensagens)
                        .path(request.getRequestURI())
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 4. JSON malformado ou tipos incompatíveis no corpo -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroPadraoDTO> tratarLeituraJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErroPadraoDTO erro =
                ErroPadraoDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .erro("JSON Inválido")
                        .mensagem(
                                "O corpo da requisição contém tipos de dados inválidos ou formatação JSON incorreta.")
                        .path(request.getRequestURI())
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 5. Parâmetro de rota ou query inválido (ex: /veiculos/abc onde id é Long) -> 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroPadraoDTO> tratarTipoIncompativel(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ErroPadraoDTO erro =
                ErroPadraoDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .erro("Parâmetro Inválido")
                        .mensagem(
                                "O parâmetro '"
                                        + ex.getName()
                                        + "' deve ser do tipo "
                                        + (ex.getRequiredType() != null
                                                ? ex.getRequiredType().getSimpleName()
                                                : "compatível"))
                        .path(request.getRequestURI())
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}
