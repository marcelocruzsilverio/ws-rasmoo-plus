package com.client.ws.rasmooplus.mapper.wsraspay;

import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.model.User;

public class CustomerMapper {

    public static CustomerDto build(User user) {

        var fullName = user.getName().split(" ");

        return CustomerDto.builder()
                .cpf(user.getCpf())
                .email(user.getEmail())
                .firstName(fullName[0])
                .lastName(fullName.length > 1 ? fullName[fullName.length - 1] : "")
                .email(user.getEmail())
                .build();
    }
}
