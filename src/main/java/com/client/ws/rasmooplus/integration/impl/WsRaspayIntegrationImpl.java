package com.client.ws.rasmooplus.integration.impl;

import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.integration.WsRaspayIntegration;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class WsRaspayIntegrationImpl implements WsRaspayIntegration {

    @Value("${webservices.raspay.host}")
    private String raspayHost;

    @Value("${webservices.raspay.v1.customer}")
    private String customerUrl;

    @Value("${webservices.raspay.v1.order}")
    private String orderUrl;

    @Value("${webservices.raspay.v1.payment}")
    private String paymentUrl;

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    public WsRaspayIntegrationImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.headers = getHttpHeaders();
    }
    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        try {
            HttpEntity<CustomerDto> request = new HttpEntity<>(customerDto, this.headers);
            ResponseEntity<CustomerDto> response = restTemplate.exchange(raspayHost+customerUrl, HttpMethod.POST, request, CustomerDto.class);
            return response.getBody();
        }catch(Exception e) {
            throw e;
        }
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        try {
            HttpEntity<OrderDto> request = new HttpEntity<>(orderDto, this.headers);
            ResponseEntity<OrderDto> response = restTemplate.exchange(raspayHost+orderUrl, HttpMethod.POST, request, OrderDto.class);
            return response.getBody();
        }catch(Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean processPayment(PaymentDto paymentDto) {
        try {
            HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto, this.headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(raspayHost+paymentUrl, HttpMethod.POST, request, Boolean.class);
            return response.getBody();
        }catch(Exception e) {
            throw e;
        }
    }

    private static @NonNull HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String credentials = "rasmooplus:r@sm00";
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }

}
