package com.example.smsauth;

import com.example.smsauth.config.SmsAuthProperties;
import com.example.smsauth.config.SolapiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		SmsAuthProperties.class,
		SolapiProperties.class
})
public class SmsAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmsAuthApplication.class, args);
	}
}