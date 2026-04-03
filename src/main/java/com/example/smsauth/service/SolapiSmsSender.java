package com.example.smsauth.service;

import com.example.smsauth.common.ResponseCode;
import com.example.smsauth.config.SolapiProperties;
import com.example.smsauth.exception.BusinessException;
import com.example.smsauth.util.SolapiAuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolapiSmsSender implements SmsSender {

    private final RestClient restClient;
    private final SolapiProperties properties;

    @Override
    public void send(String phoneNumber, String messageText) {
        validateProperties();

        String to = normalize(phoneNumber);
        String from = normalize(properties.sender());
        String authorization = SolapiAuthUtil.createAuthorizationHeader(
                properties.apiKey(),
                properties.apiSecret()
        );

        Map<String, Object> payload = Map.of(
                "messages", List.of(
                        Map.of(
                                "to", to,
                                "from", from,
                                "text", messageText
                        )
                )
        );

        try {
            String responseBody = restClient.post()
                    .uri(properties.baseUrl() + "/messages/v4/send-many")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorization)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("SOLAPI send success. to={}, response={}", to, responseBody);
        } catch (RestClientResponseException e) {
            log.error("SOLAPI send failed. to={}, status={}, body={}",
                    to,
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new BusinessException(ResponseCode.SMS_SEND_FAILED);
        } catch (Exception e) {
            log.error("Unexpected SOLAPI send error. to={}, message={}", to, e.getMessage(), e);
            throw new BusinessException(ResponseCode.SMS_SEND_FAILED);
        }
    }

    private void validateProperties() {
        if (isBlank(properties.apiKey())
                || isBlank(properties.apiSecret())
                || isBlank(properties.sender())
                || isBlank(properties.baseUrl())) {
            throw new BusinessException(
                    ResponseCode.SOLAPI_AUTH_FAILED,
                    "SOLAPI 설정값이 올바르지 않습니다."
            );
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.replaceAll("[^0-9]", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}