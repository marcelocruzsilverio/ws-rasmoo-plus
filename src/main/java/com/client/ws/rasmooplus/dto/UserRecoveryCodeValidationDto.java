package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecoveryCodeValidationDto {

    @NotBlank(message = "E-mail não pode ser nulo ou vazio")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Código não pode ser nulo ou vazio")
    private String code;
}
