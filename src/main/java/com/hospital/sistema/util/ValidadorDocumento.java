package com.hospital.sistema.util;

import com.hospital.sistema.enums.TipoDocumento;

/**
 * Utilitário para validação de documentos.
 */
public class ValidadorDocumento {

    private static final String PADRAO_BI = "\\d{9}[A-Z]{2}\\d{3}";

    private ValidadorDocumento() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Valida o formato do documento de acordo com o tipo.
     * Para Bilhete de Identidade: 9 dígitos + 2 letras + 3 dígitos
     */
    public static boolean isValido(TipoDocumento tipo, String numero) {
        if (tipo == null || numero == null || numero.isBlank()) {
            return false;
        }

        if (tipo == TipoDocumento.BILHETE_IDENTIDADE) {
            return numero.matches(PADRAO_BI);
        }

        // Para passaporte, apenas verifica se não está vazio
        return !numero.isBlank();
    }
}
