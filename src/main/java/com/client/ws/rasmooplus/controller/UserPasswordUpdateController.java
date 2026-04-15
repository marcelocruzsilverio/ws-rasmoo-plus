package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.UserNewPasswordDto;
import com.client.ws.rasmooplus.service.UserPasswordUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recovery-code")
public class UserPasswordUpdateController {

    private final UserPasswordUpdateService userPasswordUpdateService;

    public UserPasswordUpdateController(
            UserPasswordUpdateService userPasswordUpdateService) {
        this.userPasswordUpdateService = userPasswordUpdateService;
    }

    @PatchMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UserNewPasswordDto dto) {
        userPasswordUpdateService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }
}