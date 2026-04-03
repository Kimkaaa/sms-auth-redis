package com.example.smsauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sms.auth")
public record SmsAuthProperties(
        long codeExpireSeconds,
        int sendLimitCount,
        long sendLimitWindowSeconds,
        int verifyFailLimitCount,
        long verifiedExpireSeconds
) {
}