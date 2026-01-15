package com.hospital.sistema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tratador global de exceções da aplicação.
 * Centraliza o tratamento de erros e retorna respostas padronizadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return criarRespostaErro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(ValidacaoException ex) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", HttpStatus.BAD_REQUEST.value());

        if (ex.getBindingResult() != null) {
            List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                    .map(this::mapearErro)
                    .toList();
            resposta.put("errors", erros);
        } else {
            resposta.put("mensagem", ex.getMessage());
        }

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return criarRespostaErro(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DocumentoJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentoJaCadastrado(DocumentoJaCadastradoException ex) {
        return criarRespostaErro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AcessoNegadoException ex) {
        return criarRespostaErro(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        return criarRespostaErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    private ResponseEntity<Map<String, Object>> criarRespostaErro(HttpStatus status, String mensagem) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", status.value());
        resposta.put("erro", status.getReasonPhrase());
        resposta.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(resposta);
    }

    private Map<String, String> mapearErro(FieldError fieldError) {
        Map<String, String> erro = new HashMap<>();
        erro.put("campo", fieldError.getField());
        erro.put("mensagem", fieldError.getDefaultMessage());
        return erro;
    }
}
