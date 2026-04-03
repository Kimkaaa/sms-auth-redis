package com.example.smsauth.support;

import com.example.smsauth.service.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * SMS 발송 테스트용 Mock 구현체.
 * 실제 환경에서는 SolapiSmsSender를 사용하며,
 * 현재는 보관용 코드로만 유지하고 빈으로 등록하지 않는다.
 * 필요할 때 @Component를 활성화하고 local 프로필에서만 사용한다.
 */
@Profile("local")
@Slf4j
//@Component
public class MockSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        log.info("[MOCK SMS] to={}, message={}", phoneNumber, message);
    }
}