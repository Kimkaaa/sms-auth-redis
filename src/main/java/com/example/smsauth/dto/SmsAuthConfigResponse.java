package com.example.smsauth.dto;

public class SmsAuthConfigResponse {

    private final long codeExpireSeconds;
    private final int sendLimitCount;

    public SmsAuthConfigResponse(long codeExpireSeconds, int sendLimitCount) {
        this.codeExpireSeconds = codeExpireSeconds;
        this.sendLimitCount = sendLimitCount;
    }

    public long getCodeExpireSeconds() {
        return codeExpireSeconds;
    }

    public int getSendLimitCount() {
        return sendLimitCount;
    }
}