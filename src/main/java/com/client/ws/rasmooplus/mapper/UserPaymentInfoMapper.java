package com.client.ws.rasmooplus.mapper;

import com.client.ws.rasmooplus.dto.UserPaymentInfoDto;
import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.model.UserPaymentInfo;

public class UserPaymentInfoMapper {
    public static UserPaymentInfo fromDtoToEntity(UserPaymentInfoDto userPaymentInfoDto, User user) {
        return UserPaymentInfo.builder()
                .id(userPaymentInfoDto.getId())
                .cardNumber(userPaymentInfoDto.getCardNumber())
                .cardExpirationMonth(userPaymentInfoDto.getCardExpirationMonth())
                .cardExpirationYear(userPaymentInfoDto.getCardExpirationYear())
                .cardSecurityCode(userPaymentInfoDto.getCardSecurityCode())
                .price(userPaymentInfoDto.getPrice())
                .dtPayment(userPaymentInfoDto.getDtPayment())
                .user(user)
                .build();
    }
}
