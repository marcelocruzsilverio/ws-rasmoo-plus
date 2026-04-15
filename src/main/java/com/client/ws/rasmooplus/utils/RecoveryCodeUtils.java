package com.client.ws.rasmooplus.utils;

import java.util.Random;

public final class RecoveryCodeUtils {

    public static final String REDIS_KEY_PREFIX = "recovery-code:";

    // Construtor privado: impede instanciação — padrão utils
    private RecoveryCodeUtils() {
        throw new UnsupportedOperationException(
                "RecoveryCodeUtils é uma classe utilitária e não pode ser instanciada");
    }

    public static String generate4DigitsCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
}