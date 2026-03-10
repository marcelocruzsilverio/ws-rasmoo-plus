package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.PaymentProcessDto;
import com.client.ws.rasmooplus.exception.BusinessException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.repository.UserRepository;
import com.client.ws.rasmooplus.service.PaymentInfoService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private final UserRepository userRepository;

    PaymentInfoServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public Boolean process(PaymentProcessDto paymentProcessDto) {
        //verifica usuario por id e verifica se já existe assinatura
        var userOpt = userRepository.findById(paymentProcessDto.getUserPaymentInfoDto().getId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOpt.get();
        if (Objects.nonNull(user.getSubscriptionType())) {
            throw new BusinessException("User subscription type already exists");
        }
        //salvar informações de pagamento
        //cria ou  atualiza usuario raspay
        //cria o pedido de pagamento
        //processa o pagamento
        //enviar email de criação de conta
        //retorna o sucesso ou não do pagamento

        return null;
    }
}
