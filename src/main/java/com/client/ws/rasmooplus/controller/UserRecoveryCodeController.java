package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.UserRecoveryCodeDto;
import com.client.ws.rasmooplus.service.UserRecoveryCodeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recovery-code")
public class UserRecoveryCodeController {

    private final UserRecoveryCodeService userRecoveryCodeService;

    public UserRecoveryCodeController(UserRecoveryCodeService userRecoveryCodeService) {
        this.userRecoveryCodeService = userRecoveryCodeService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendRecoveryCode(
            @Valid @RequestBody UserRecoveryCodeDto dto) {
        userRecoveryCodeService.sendRecoveryCode(dto.getEmail());
        return ResponseEntity.noContent().build();
    }
}
