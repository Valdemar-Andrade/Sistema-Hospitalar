package com.hospital.sistema.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilitário para criptografia e verificação de senhas.
 */
public class SenhaUtils {

    private SenhaUtils() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Criptografa uma senha usando SHA-256.
     */
    public static String criptografar(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }

    /**
     * Verifica se a senha informada corresponde à senha criptografada.
     */
    public static boolean verificar(String senhaDigitada, String senhaCriptografada) {
        String senhaDigitadaCriptografada = criptografar(senhaDigitada);
        return senhaDigitadaCriptografada.equals(senhaCriptografada);
    }
}
