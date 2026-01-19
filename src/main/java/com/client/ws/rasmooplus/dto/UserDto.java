package com.client.ws.rasmooplus.dto;

import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.model.UserType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Name can't be null or empty")
    @Size(min = 5, message = "Name size must be greater than 5")
    private String name;

    @Email(message = "Email invalid")
    private String email;

    @Size(min = 11, message = "Phone size must be greater than 10")
    private String phone;

    @CPF(message = "CPF invalid")
    private String cpf;

    private LocalDate dtSubscription = LocalDate.now();

    private LocalDate dtExpiration  = LocalDate.now();

    @NotNull(message = "User type can't be null or empty")
    private Long userTypeId;

    private Long subscriptionTypeId;
}
