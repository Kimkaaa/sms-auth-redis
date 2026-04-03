package com.example.smsauth.dto;

public class SmsSendAvailabilityResponse {

    private final int currentSendCount;
    private final int remainingSendCount;
    private final int maxSendLimitCount;

    public SmsSendAvailabilityResponse(int currentSendCount, int remainingSendCount, int maxSendLimitCount) {
        this.currentSendCount = currentSendCount;
        this.remainingSendCount = remainingSendCount;
        this.maxSendLimitCount = maxSendLimitCount;
    }

    public int getCurrentSendCount() {
        return currentSendCount;
    }

    public int getRemainingSendCount() {
        return remainingSendCount;
    }

    public int getMaxSendLimitCount() {
        return maxSendLimitCount;
    }
}