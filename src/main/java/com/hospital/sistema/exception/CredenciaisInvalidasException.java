package com.hospital.sistema.exception;

/**
 * Exceção lançada quando as credenciais de login são inválidas.
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais inválidas");
    }

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
