package com.hospital.sistema.exception;

/**
 * Exceção lançada quando um documento já está cadastrado no sistema.
 */
public class DocumentoJaCadastradoException extends RuntimeException {

    public DocumentoJaCadastradoException() {
        super("Número do documento já cadastrado no sistema");
    }

    public DocumentoJaCadastradoException(String numeroDocumento) {
        super("O documento " + numeroDocumento + " já está cadastrado no sistema");
    }
}
