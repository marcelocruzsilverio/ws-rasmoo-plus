package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.service.SubscriptionTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/subscription-type")
public class SubscriptionTypeController {

    private final SubscriptionTypeService subscriptionTypeService;

    public SubscriptionTypeController(SubscriptionTypeService subscriptionTypeService) {
        this.subscriptionTypeService = subscriptionTypeService;
    }

    @GetMapping()
    public ResponseEntity<List<SubscriptionType>> findyAll() {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionType> findyById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionTypeService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<SubscriptionType> createSubscriptionType(@RequestBody SubscriptionTypeDto subscriptionTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionTypeService.createSubscriptionType(subscriptionTypeDto));
    }
}
