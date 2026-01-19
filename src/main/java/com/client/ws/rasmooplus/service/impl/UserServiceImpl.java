package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.UserDto;
import com.client.ws.rasmooplus.exception.BadRequestException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.mapper.UserMapper;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.UserRepository;
import com.client.ws.rasmooplus.repository.UserTypeRepository;
import com.client.ws.rasmooplus.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserTypeRepository userTypeRepository;

    public UserServiceImpl(UserRepository userRepository, UserTypeRepository userTypeRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public User createUser(UserDto userDto) {
        if (Objects.nonNull(userDto.getId())) {
            throw  new BadRequestException("User Id needs to be null");
        }

        Optional<UserType> userTypeId = userTypeRepository.findById(userDto.getUserTypeId());

        if (userTypeId.isEmpty()) {
            throw  new NotFoundException("UserTypeId not found");
        }
        UserType userTypeEntity = userTypeId.get();

        return userRepository.save(UserMapper.fromDtoToEntity(userDto, userTypeEntity, null));
    }
}
