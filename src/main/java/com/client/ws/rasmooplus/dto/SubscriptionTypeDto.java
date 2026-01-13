package com.client.ws.rasmooplus.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionTypeDto {

    private Long id;

    @NotBlank(message = "Name can't be null or empty")
    @Size(min = 5, max = 30, message = "Name size must be between 5 and 30")
    private String name;

    @Max(value = 12, message = "Access month can't be greater than 12")
    private Long accessMonths;

    @NotNull(message = "Price can't be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Product Key can't be null or empty")
    @Size(min = 5, max = 15, message = "Product key size must be between 5 and 15")
    private String productKey;
}
