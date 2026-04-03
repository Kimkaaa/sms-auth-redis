package com.example.smsauth.service;

import com.example.smsauth.common.ResponseCode;
import com.example.smsauth.config.SmsAuthProperties;
import com.example.smsauth.dto.ApiResponse;
import com.example.smsauth.dto.SmsSendAvailabilityResponse;
import com.example.smsauth.exception.BusinessException;
import com.example.smsauth.util.PhoneNumberUtils;
import com.example.smsauth.util.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SmsAuthService {

    private final SmsSender smsSender;
    private final SmsAuthStore smsAuthStore;
    private final SmsAuthProperties properties;

    public ApiResponse sendVerificationCode(String rawPhoneNumber) {
        String phoneNumber = validateAndNormalizePhoneNumber(rawPhoneNumber);

        int sendCount = smsAuthStore.getSendCount(phoneNumber);
        if (sendCount >= properties.sendLimitCount()) {
            throw new BusinessException(ResponseCode.SEND_LIMIT_EXCEEDED);
        }

        String code = VerificationCodeGenerator.generate6DigitCode();
        String message = "[SMS 인증] 인증번호는 [" + code + "] 입니다. 3분 내에 입력해주세요.";

        smsSender.send(phoneNumber, message);

        smsAuthStore.saveCode(
                phoneNumber,
                code,
                Duration.ofSeconds(properties.codeExpireSeconds())
        );

        smsAuthStore.clearVerifyFail(phoneNumber);
        smsAuthStore.clearVerified(phoneNumber);
        smsAuthStore.initOrIncreaseSendCount(
                phoneNumber,
                Duration.ofSeconds(properties.sendLimitWindowSeconds())
        );

        return ApiResponse.success(ResponseCode.SMS_SENT);
    }

    public ApiResponse verifyCode(String rawPhoneNumber, String inputCode) {
        String phoneNumber = validateAndNormalizePhoneNumber(rawPhoneNumber);

        if (inputCode == null || !inputCode.matches("^\\d{6}$")) {
            throw new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }

        String savedCode = smsAuthStore.getCode(phoneNumber);

        if (savedCode == null) {
            throw new BusinessException(ResponseCode.CODE_EXPIRED);
        }

        if (savedCode.equals(inputCode)) {
            smsAuthStore.clearCode(phoneNumber);
            smsAuthStore.clearVerifyFail(phoneNumber);
            smsAuthStore.markVerified(
                    phoneNumber,
                    Duration.ofSeconds(properties.verifiedExpireSeconds())
            );
            return ApiResponse.success(ResponseCode.VERIFIED);
        }

        long failCount = smsAuthStore.increaseVerifyFail(
                phoneNumber,
                Duration.ofSeconds(properties.codeExpireSeconds())
        );

        if (failCount >= properties.verifyFailLimitCount()) {
            smsAuthStore.clearCode(phoneNumber);
            smsAuthStore.clearVerifyFail(phoneNumber);
            throw new BusinessException(ResponseCode.VERIFY_LIMIT_EXCEEDED);
        }

        throw new BusinessException(ResponseCode.INVALID_CODE);
    }

    public ApiResponse checkVerified(String rawPhoneNumber) {
        String phoneNumber = validateAndNormalizePhoneNumber(rawPhoneNumber);

        if (smsAuthStore.isVerified(phoneNumber)) {
            return ApiResponse.success(ResponseCode.VERIFIED_STATUS);
        }

        return ApiResponse.fail(ResponseCode.NOT_VERIFIED);
    }

    public long getCodeExpireSeconds() {
        return properties.codeExpireSeconds();
    }

    public int getSendLimitCount() {
        return properties.sendLimitCount();
    }

    public SmsSendAvailabilityResponse getSendAvailability(String rawPhoneNumber) {
        String phoneNumber = validateAndNormalizePhoneNumber(rawPhoneNumber);

        int currentSendCount = smsAuthStore.getSendCount(phoneNumber);
        int maxSendLimitCount = properties.sendLimitCount();
        int remainingSendCount = Math.max(maxSendLimitCount - currentSendCount, 0);

        return new SmsSendAvailabilityResponse(
                currentSendCount,
                remainingSendCount,
                maxSendLimitCount
        );
    }

    private String validateAndNormalizePhoneNumber(String rawPhoneNumber) {
        String phoneNumber = PhoneNumberUtils.normalize(rawPhoneNumber);

        if (!PhoneNumberUtils.isValidMobile(phoneNumber)) {
            throw new BusinessException(ResponseCode.INVALID_PHONE_NUMBER);
        }

        return phoneNumber;
    }
}