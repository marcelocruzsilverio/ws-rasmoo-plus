package com.client.ws.rasmooplus.service;

import com.client.ws.rasmooplus.dto.UserNewPasswordDto;

public interface UserPasswordUpdateService {
    void updatePassword(UserNewPasswordDto dto);
}
