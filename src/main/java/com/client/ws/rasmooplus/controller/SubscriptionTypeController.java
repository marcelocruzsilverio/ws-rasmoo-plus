package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.service.SubscriptionTypeService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "subscriptionType")
    @GetMapping()
    public ResponseEntity<List<SubscriptionType>> findyAll() {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionTypeService.findAll());
    }

    @Cacheable(value = "subscriptionType", key = "#id")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionType> findyById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionTypeService.findById(id));
    }

    @CacheEvict(value = "subscriptionType", allEntries = true)
    @PostMapping()
    public ResponseEntity<SubscriptionType> createSubscriptionType(@Valid @RequestBody SubscriptionTypeDto subscriptionTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionTypeService.createSubscriptionType(subscriptionTypeDto));
    }

    @CacheEvict(value = "subscriptionType", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionType> updateSubscriptionType(@PathVariable Long id, @RequestBody SubscriptionTypeDto subscriptionTypeDto) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionTypeService.updateSubscriptionType(id, subscriptionTypeDto));
    }

    @CacheEvict(value = "subscriptionType", allEntries = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionType(@PathVariable Long id) {
        subscriptionTypeService.deleteSubscriptionType(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
