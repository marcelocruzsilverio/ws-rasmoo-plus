package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.integration.impl.WsRaspayIntegrationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class WsRaspayIntegrationImplTest {

    @Autowired
    private WsRaspayIntegrationImpl wsRaspayIntegrationImpl;

    @Test
    void createCustomerWhenDtoOk() {
        CustomerDto customerDto = new CustomerDto(null, "02371386030", "teste@teste", "Marcelo", "Cruz");
        wsRaspayIntegrationImpl.createCustomer(customerDto);
    }

    @Test
    void createOrderWhenDtoOk() {
        OrderDto orderDto = new OrderDto(null,"698e61c614e7804bdf458496", BigDecimal.ZERO, "MONTH22");
        wsRaspayIntegrationImpl.createOrder(orderDto);
    }
}
