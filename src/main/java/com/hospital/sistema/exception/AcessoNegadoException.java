package com.hospital.sistema.exception;

/**
 * Exceção lançada quando o usuário não tem permissão para acessar um recurso.
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException() {
        super("Acesso negado. Você não tem permissão para acessar este recurso.");
    }

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
