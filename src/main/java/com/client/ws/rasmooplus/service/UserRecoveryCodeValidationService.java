package com.client.ws.rasmooplus.service;

public interface UserRecoveryCodeValidationService {
    Boolean validateRecoveryCode(String email, String code);
}
