package com.example.smsauth.service;

public interface SmsSender {
    void send(String phoneNumber, String message);
}