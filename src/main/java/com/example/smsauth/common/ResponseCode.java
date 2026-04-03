package com.example.smsauth.common;

public enum ResponseCode {

    SMS_SENT("SMS_SENT", "인증번호가 발송되었습니다."),
    VERIFIED("VERIFIED", "인증이 완료되었습니다."),
    VERIFIED_STATUS("VERIFIED_STATUS", "인증 완료 상태입니다."),
    NOT_VERIFIED("NOT_VERIFIED", "미인증 상태입니다."),
    INVALID_PHONE_NUMBER("INVALID_PHONE_NUMBER", "올바른 휴대폰 번호 형식이 아닙니다."),
    INVALID_VERIFICATION_CODE("INVALID_VERIFICATION_CODE", "인증번호는 6자리 숫자여야 합니다."),
    INVALID_CODE("INVALID_CODE", "인증번호가 올바르지 않습니다."),
    CODE_EXPIRED("CODE_EXPIRED", "인증번호가 만료되었거나 존재하지 않습니다."),
    SEND_LIMIT_EXCEEDED("SEND_LIMIT_EXCEEDED", "인증 요청 가능 횟수를 초과했습니다."),
    VERIFY_LIMIT_EXCEEDED("VERIFY_LIMIT_EXCEEDED", "인증 시도 횟수를 초과했습니다. 다시 인증번호를 요청해주세요."),
    SMS_SEND_FAILED("SMS_SEND_FAILED", "문자 발송 중 오류가 발생했습니다."),
    SOLAPI_AUTH_FAILED("SOLAPI_AUTH_FAILED", "문자 인증 헤더 생성 중 오류가 발생했습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");

    private final String code;
    private final String defaultMessage;

    ResponseCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}