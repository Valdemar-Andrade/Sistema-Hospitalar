package com.hospital.sistema.exception;

import org.springframework.validation.BindingResult;

/**
 * Exceção lançada quando ocorrem erros de validação.
 */
public class ValidacaoException extends RuntimeException {

    private final BindingResult bindingResult;

    public ValidacaoException(BindingResult bindingResult) {
        super("Erro de validação");
        this.bindingResult = bindingResult;
    }

    public ValidacaoException(String mensagem) {
        super(mensagem);
        this.bindingResult = null;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
