package com.hospital.sistema.exception;

/**
 * Exceção lançada quando um recurso não é encontrado no sistema.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " não encontrado(a) com ID: " + id);
    }
}
