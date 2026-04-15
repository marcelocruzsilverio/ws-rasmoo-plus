package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecoveryCodeDto {

    @NotBlank(message = "E-mail não pode ser nulo ou vazio")
    @Email(message = "E-mail inválido")
    private String email;
}