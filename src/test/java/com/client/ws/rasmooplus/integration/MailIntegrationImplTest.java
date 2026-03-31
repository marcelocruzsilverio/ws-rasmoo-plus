package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.wsraspay.CreditCardDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.impl.MailIntegrationImpl;
import com.client.ws.rasmooplus.integration.impl.WsRaspayIntegrationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class MailIntegrationImplTest {

    @Autowired
    private MailIntegrationImpl mailIntegrationImpl;

    @Test
    void createCustomerWhenDtoOk() {
        mailIntegrationImpl.send("marcelocruzprogramador@gmail.com", "Ola Gmail", "Acesso Liberado");
    }

}
