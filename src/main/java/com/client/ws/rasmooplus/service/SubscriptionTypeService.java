package com.client.ws.rasmooplus.service;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.model.SubscriptionType;

import java.util.List;

public interface SubscriptionTypeService {
    List<SubscriptionType> findAll();

    SubscriptionType findById(Long id);

    SubscriptionType updateSubscriptionType(Long id, SubscriptionTypeDto subscriptionTypeDto);

    SubscriptionType createSubscriptionType(SubscriptionTypeDto subscriptionTypeDto);

    void deleteSubscriptionType(Long id);
}
