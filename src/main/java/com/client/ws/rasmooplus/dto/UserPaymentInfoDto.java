package com.client.ws.rasmooplus.dto;

import com.client.ws.rasmooplus.model.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentInfoDto {

    private Long id;

    @Size(min = 16, max = 16, message = "Card number must have 16 characters")
    private String cardNumber;

    @Min(value = 1)
    @Max(value = 12)
    private Long cardExpirationMonth;

    private Long cardExpirationYear;

    @Size(min = 3, max = 3, message = "Card security code must have 3 characters")
    private String cardSecurityCode;

    private BigDecimal price;

    private Long instalments;

    private LocalDate dtPayment = LocalDate.now();

    @NotNull(message = "User id can't be null")
    private Long userId;
}
