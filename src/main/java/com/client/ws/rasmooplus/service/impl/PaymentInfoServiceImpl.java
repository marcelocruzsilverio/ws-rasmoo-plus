package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.dto.wsraspay.CustomerDto;
import com.client.ws.rasmooplus.dto.wsraspay.OrderDto;
import com.client.ws.rasmooplus.dto.wsraspay.PaymentDto;
import com.client.ws.rasmooplus.enums.UserTypeEnum;
import com.client.ws.rasmooplus.exception.BusinessException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.integration.MailIntegration;
import com.client.ws.rasmooplus.integration.WsRaspayIntegration;
import com.client.ws.rasmooplus.mapper.UserPaymentInfoMapper;
import com.client.ws.rasmooplus.mapper.wsraspay.CreditCardMapper;
import com.client.ws.rasmooplus.mapper.wsraspay.CustomerMapper;
import com.client.ws.rasmooplus.mapper.wsraspay.OrderMapper;
import com.client.ws.rasmooplus.mapper.wsraspay.PaymentMapper;
import com.client.ws.rasmooplus.model.SubscriptionType;
import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.model.UserCredentials;
import com.client.ws.rasmooplus.model.UserPaymentInfo;
import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.SubscriptionTypeRepository;
import com.client.ws.rasmooplus.repository.UserCredentialsRepository;
import com.client.ws.rasmooplus.repository.UserPaymentInfoRepository;
import com.client.ws.rasmooplus.repository.UserRepository;
import com.client.ws.rasmooplus.repository.UserTypeRepository;
import com.client.ws.rasmooplus.service.PaymentInfoService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Value("${webservices.rasplus.default.password}")
    private String defaultPassword;

    private final UserRepository userRepository;
    private final UserPaymentInfoRepository userPaymentInfoRepository;
    private final WsRaspayIntegration wsRaspayIntegration;
    private final MailIntegration mailIntegration;
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserTypeRepository userTypeRepository;
    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public PaymentInfoServiceImpl(UserRepository userRepository,
                                  UserPaymentInfoRepository userPaymentInfoRepository,
                                  WsRaspayIntegration wsRaspayIntegration,
                                  MailIntegration mailIntegration,
                                  UserCredentialsRepository userCredentialsRepository,
                                  UserTypeRepository userTypeRepository,
                                  SubscriptionTypeRepository subscriptionTypeRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPaymentInfoRepository = userPaymentInfoRepository;
        this.wsRaspayIntegration = wsRaspayIntegration;
        this.mailIntegration = mailIntegration;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userTypeRepository = userTypeRepository;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Boolean process(PaymentProcessDto paymentProcessDto) {

        User user = getUserAndValidate(paymentProcessDto);

        Boolean successPayment = processPaymentOnGateway(paymentProcessDto, user);

        if (successPayment) {
            savePaymentInfo(paymentProcessDto, user);
            createUserCredentials(user);
            updateUserSubscription(paymentProcessDto, user);
            sendAccessEmail(user);
            return true;
        }

        return false;
    }

    private User getUserAndValidate(PaymentProcessDto paymentProcessDto) {
        User user = userRepository.findById(paymentProcessDto.getUserPaymentInfoDto().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (Objects.nonNull(user.getSubscriptionType())) {
            throw new BusinessException("User subscription type already exists");
        }

        return user;
    }

    private Boolean processPaymentOnGateway(PaymentProcessDto paymentProcessDto, User user) {
        CustomerDto customerDto = wsRaspayIntegration.createCustomer(CustomerMapper.build(user));
        OrderDto orderDto = wsRaspayIntegration.createOrder(OrderMapper.build(customerDto.getId(), paymentProcessDto));
        PaymentDto paymentDto = PaymentMapper.build(
                CreditCardMapper.build(paymentProcessDto, user.getCpf()),
                orderDto.getCustomerId(),
                orderDto.getId()
        );
        return wsRaspayIntegration.processPayment(paymentDto);
    }

    private void savePaymentInfo(PaymentProcessDto paymentProcessDto, User user) {
        UserPaymentInfo userPaymentInfo = UserPaymentInfoMapper
                .fromDtoToEntity(paymentProcessDto.getUserPaymentInfoDto(), user);
        userPaymentInfoRepository.save(userPaymentInfo);
    }

    private void createUserCredentials(User user) {
        UserType userType = userTypeRepository.findById(UserTypeEnum.ALUNO.getId())
                .orElseThrow(() -> new NotFoundException("UserType não encontrado"));

        UserCredentials userCredentials = new UserCredentials(
                null,
                user.getEmail(),
                passwordEncoder.encode(defaultPassword),
                userType
        );
        userCredentialsRepository.save(userCredentials);
    }

    private void updateUserSubscription(PaymentProcessDto paymentProcessDto, User user) {
        SubscriptionType subscriptionType = subscriptionTypeRepository
                .findByProductKey(paymentProcessDto.getProductKey())
                .orElseThrow(() -> new NotFoundException("Subscription type não encontrado"));

        user.setSubscriptionType(subscriptionType);
        userRepository.save(user);
    }

    private void sendAccessEmail(User user) {
        mailIntegration.send(
                user.getEmail(),
                "Usuário: " + user.getEmail() + " - Senha: " + defaultPassword,
                "Acesso Liberado!"
        );
    }
}