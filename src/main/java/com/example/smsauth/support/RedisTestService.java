package com.example.smsauth.support;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis 연결 및 간단한 read/write 동작 확인용 테스트 서비스.
 * local 프로필에서만 활성화되며, 실제 인증 로직에서는 사용하지 않는다.
 */
@Profile("local")
@Service
@RequiredArgsConstructor
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public String saveTestValue() {
        redisTemplate.opsForValue().set("test:key", "hello redis");
        return redisTemplate.opsForValue().get("test:key");
    }
}