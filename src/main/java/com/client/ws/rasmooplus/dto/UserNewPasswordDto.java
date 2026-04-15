package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNewPasswordDto {

    @NotBlank(message = "E-mail não pode ser nulo ou vazio")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Código não pode ser nulo ou vazio")
    private String recoveryCode;

    @NotBlank(message = "Nova senha não pode ser nula ou vazia")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String newPassword;
}
