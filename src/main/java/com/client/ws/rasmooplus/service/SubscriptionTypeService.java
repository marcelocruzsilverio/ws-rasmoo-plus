package com.client.ws.rasmooplus.service;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.model.SubscriptionType;

import java.util.List;

public interface SubscriptionTypeService {
    List<SubscriptionType> findAll();

    SubscriptionType findById(Long id);

    SubscriptionType updateSubscriptionType(Long id, SubscriptionType subscriptionType);

    SubscriptionType createSubscriptionType(SubscriptionTypeDto subscriptionTypeDto);

    SubscriptionType deleteSubscriptionType(Long id);
}
