package com.example.smsauth.util;

public final class SmsRedisKeys {

    private SmsRedisKeys() {
    }

    public static String code(String phoneNumber) {
        return "sms:code:" + phoneNumber;
    }

    public static String sendCount(String phoneNumber) {
        return "sms:send-count:" + phoneNumber;
    }

    public static String verifyFail(String phoneNumber) {
        return "sms:verify-fail:" + phoneNumber;
    }

    public static String verified(String phoneNumber) {
        return "sms:verified:" + phoneNumber;
    }
}