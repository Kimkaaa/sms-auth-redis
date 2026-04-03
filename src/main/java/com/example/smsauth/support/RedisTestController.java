package com.example.smsauth.support;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redis 연결 및 동작 확인용 테스트 컨트롤러.
 * local 프로필에서만 활성화되며, 실제 서비스 API로 사용하지 않는다.
 */
@Profile("local")
@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTestService redisTestService;

    @GetMapping("/redis-test")
    public String redisTest() {
        return redisTestService.saveTestValue();
    }
}