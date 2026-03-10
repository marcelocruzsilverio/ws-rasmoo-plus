package com.client.ws.rasmooplus.mapper.wsraspay;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;

public class CreditCardMapper {
    public static CreditCardDto build(PaymentProcessDto paymentProcessDto, String documentNumber) {
        return CreditCardDto.builder()
                .documentNumber(documentNumber)
                .cvv(Long.valueOf(paymentProcessDto.getUserPaymentInfoDto().getCardSecurityCode()))
                .month(paymentProcessDto.getUserPaymentInfoDto().getCardExpirationMonth())
                .year(paymentProcessDto.getUserPaymentInfoDto().getCardExpirationYear())
                .number(paymentProcessDto.getUserPaymentInfoDto().getCardNumber())
                .installments(paymentProcessDto.getUserPaymentInfoDto().getInstalments())
                .build();
    }
}
