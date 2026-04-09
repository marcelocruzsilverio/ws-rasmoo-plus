package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.SubscriptionTypeDto;
import com.client.ws.rasmooplus.exception.BadRequestException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.mapper.SubscriptionTypeMapper;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.repository.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.service.SubscriptionTypeService;
import lombok.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "subscriptionType")
    public List<SubscriptionType> findAll() {
        return subscriptionTypeRepository.findAll();
    }

    @Override
    @Cacheable(value = "subscriptionType", key = "#id")
    public SubscriptionType findById(Long id) {
        return getSubscriptionType(id);
    }



    @Override
    @CacheEvict(value = "subscriptionType", allEntries = true)
    public SubscriptionType updateSubscriptionType(Long id, SubscriptionTypeDto subscriptionTypeDto) {
        getSubscriptionType(id);
        subscriptionTypeDto.setId(id);
        return subscriptionTypeRepository.save(SubscriptionTypeMapper.fromDtoToEntity(subscriptionTypeDto));

    }

    @Override
    @CacheEvict(value = "subscriptionType", allEntries = true)
    public SubscriptionType createSubscriptionType(SubscriptionTypeDto subscriptionTypeDto) {
        if (Objects.nonNull(subscriptionTypeDto.getId())) {
            throw  new BadRequestException("SubscriptionType Id needs to be null");
        }
        return subscriptionTypeRepository.save(SubscriptionTypeMapper.fromDtoToEntity(subscriptionTypeDto));
    }

    @Override
    @CacheEvict(value = "subscriptionType", allEntries = true)
    public void deleteSubscriptionType(Long id) {
        getSubscriptionType(id);
        subscriptionTypeRepository.deleteById(id);
    }

    private @NonNull SubscriptionType getSubscriptionType(Long id) {
        Optional<SubscriptionType> subscriptionType = subscriptionTypeRepository.findById(id);

        if (subscriptionType.isEmpty()) {
            throw new NotFoundException("SubscriptionType not found");
        }
        return subscriptionType.get();
    }
}
