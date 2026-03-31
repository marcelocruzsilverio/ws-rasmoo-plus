package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.service.PaymentInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment")
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;

    public PaymentInfoController(PaymentInfoService paymentInfoService) {
        this.paymentInfoService = paymentInfoService;
    }

    @PostMapping("/process")
    public ResponseEntity<Boolean> process(@RequestBody PaymentProcessDto paymentProcessDto) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentInfoService.process(paymentProcessDto));
    }
}
