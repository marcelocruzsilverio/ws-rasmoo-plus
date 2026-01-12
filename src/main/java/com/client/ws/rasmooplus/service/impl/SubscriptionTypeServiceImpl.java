package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.exception.BadRequestException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.repository.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.service.SubscriptionTypeService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubscriptionTypeServiceImpl implements SubscriptionTypeService {

    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public SubscriptionTypeServiceImpl(SubscriptionTypeRepository subscriptionTypeRepository) {
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }


    @Override
    public List<SubscriptionType> findAll() {
        return subscriptionTypeRepository.findAll();
    }

    @Override
    public SubscriptionType findById(Long id) {
        return getSubscriptionType(id);
    }



    @Override
    public SubscriptionType updateSubscriptionType(Long id, SubscriptionTypeDto subscriptionTypeDto) {
        getSubscriptionType(id);

        return subscriptionTypeRepository.save(SubscriptionType.builder()
                .id(id)
                .name(subscriptionTypeDto.getName())
                .price(subscriptionTypeDto.getPrice())
                .accessMonths(subscriptionTypeDto.getAccessMonths())
                .productKey(subscriptionTypeDto.getProductKey())
                .build());

    }

    @Override
    public SubscriptionType createSubscriptionType(SubscriptionTypeDto subscriptionTypeDto) {
        if (Objects.nonNull(subscriptionTypeDto.getId())) {
            throw  new BadRequestException("SubscriptionType Id needs to be null");
        }
        return subscriptionTypeRepository.save(SubscriptionType.builder()
                        .id(subscriptionTypeDto.getId())
                        .name(subscriptionTypeDto.getName())
                        .price(subscriptionTypeDto.getPrice())
                        .accessMonths(subscriptionTypeDto.getAccessMonths())
                        .productKey(subscriptionTypeDto.getProductKey())
                        .build());
    }

    @Override
    public SubscriptionType deleteSubscriptionType(Long id) {
        return null;
    }

    private @NonNull SubscriptionType getSubscriptionType(Long id) {
        Optional<SubscriptionType> subscriptionType = subscriptionTypeRepository.findById(id);

        if (subscriptionType.isEmpty()) {
            throw new NotFoundException("SubscriptionType not found");
        }
        return subscriptionType.get();
    }
}
