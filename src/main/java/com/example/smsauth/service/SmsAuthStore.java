package com.example.smsauth.service;

import com.example.smsauth.util.SmsRedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsAuthStore {

    private final StringRedisTemplate redisTemplate;

    public int getSendCount(String phoneNumber) {
        return parseInt(redisTemplate.opsForValue().get(SmsRedisKeys.sendCount(phoneNumber)));
    }

    public void initOrIncreaseSendCount(String phoneNumber, Duration ttl) {
        String key = SmsRedisKeys.sendCount(phoneNumber);
        int currentCount = getSendCount(phoneNumber);

        if (currentCount == 0) {
            redisTemplate.opsForValue().set(key, "1", ttl);
            return;
        }

        redisTemplate.opsForValue().increment(key);
    }

    public void saveCode(String phoneNumber, String code, Duration ttl) {
        redisTemplate.opsForValue().set(SmsRedisKeys.code(phoneNumber), code, ttl);
    }

    public String getCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(SmsRedisKeys.code(phoneNumber));
    }

    public void clearCode(String phoneNumber) {
        redisTemplate.delete(SmsRedisKeys.code(phoneNumber));
    }

    public void clearVerifyFail(String phoneNumber) {
        redisTemplate.delete(SmsRedisKeys.verifyFail(phoneNumber));
    }

    public long increaseVerifyFail(String phoneNumber, Duration ttl) {
        String key = SmsRedisKeys.verifyFail(phoneNumber);
        Long failCount = redisTemplate.opsForValue().increment(key);

        if (failCount != null && failCount == 1L) {
            redisTemplate.expire(key, ttl);
        }

        return failCount == null ? 0L : failCount;
    }

    public void markVerified(String phoneNumber, Duration ttl) {
        redisTemplate.opsForValue().set(SmsRedisKeys.verified(phoneNumber), "true", ttl);
    }

    public boolean isVerified(String phoneNumber) {
        return "true".equals(redisTemplate.opsForValue().get(SmsRedisKeys.verified(phoneNumber)));
    }

    public void clearVerified(String phoneNumber) {
        redisTemplate.delete(SmsRedisKeys.verified(phoneNumber));
    }

    private int parseInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Redis value is not a valid integer. value={}", value);
            return 0;
        }
    }
}