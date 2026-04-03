package com.example.smsauth.util;

import java.security.SecureRandom;

public final class VerificationCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private VerificationCodeGenerator() {
    }

    public static String generate6DigitCode() {
        int value = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(value);
    }
}