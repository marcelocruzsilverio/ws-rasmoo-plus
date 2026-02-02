package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;

public interface WsRaspayIntegration {
    CustomerDto createCustomer(CustomerDto customerDto);

    OrderDto createOrder(OrderDto orderDto);

    Boolean processPayment(PaymentDto paymentDto);
}
