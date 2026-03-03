package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
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

    @Test
    void processPaymentWhenDtoOk() {
        CreditCardDto creditCardDto = new CreditCardDto(123L, "02371386030", 0L, 06L, "1234123412341234", 2026L);
        PaymentDto paymentDto = new PaymentDto(creditCardDto, "698e61c614e7804bdf458496", "69a739699a92ad0d30609783");
        wsRaspayIntegrationImpl.processPayment(paymentDto);
    }
}
