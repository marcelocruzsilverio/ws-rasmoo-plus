package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.UserRecoveryCodeValidationDto;
import com.client.ws.rasmooplus.service.UserRecoveryCodeValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recovery-code")
public class UserRecoveryCodeValidationController {

    private final UserRecoveryCodeValidationService validationService;

    public UserRecoveryCodeValidationController(
            UserRecoveryCodeValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateCode(
            @Valid @RequestBody UserRecoveryCodeValidationDto dto) {
        Boolean isValid = validationService.validateRecoveryCode(
                dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(isValid);
    }
}
