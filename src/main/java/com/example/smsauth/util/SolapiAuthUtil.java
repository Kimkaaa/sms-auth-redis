package com.example.smsauth.util;

import com.example.smsauth.common.ResponseCode;
import com.example.smsauth.exception.BusinessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class SolapiAuthUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private SolapiAuthUtil() {
    }

    public static String createDate() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public static String createSalt(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(SALT_CHARS.charAt(RANDOM.nextInt(SALT_CHARS.length())));
        }
        return sb.toString();
    }

    public static String createSignature(String date, String salt, String apiSecret) {
        try {
            String data = date + salt;

            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(
                    apiSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA256
            );
            mac.init(keySpec);

            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (Exception e) {
            throw new BusinessException(
                    ResponseCode.SOLAPI_AUTH_FAILED,
                    "SOLAPI signature 생성에 실패했습니다."
            );
        }
    }

    public static String createAuthorizationHeader(String apiKey, String apiSecret) {
        String date = createDate();
        String salt = createSalt(32);
        String signature = createSignature(date, salt, apiSecret);

        return "HMAC-SHA256 apiKey=" + apiKey
                + ", date=" + date
                + ", salt=" + salt
                + ", signature=" + signature;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}