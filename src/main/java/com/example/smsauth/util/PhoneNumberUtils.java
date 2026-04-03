package com.example.smsauth.util;

public final class PhoneNumberUtils {

    private PhoneNumberUtils() {
    }

    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    public static boolean isValidMobile(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^01[0-9]{8,9}$");
    }
}