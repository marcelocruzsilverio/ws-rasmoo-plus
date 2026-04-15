package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.integration.MailIntegration;
import com.client.ws.rasmooplus.repository.UserCredentialsRepository;
import com.client.ws.rasmooplus.service.UserRecoveryCodeService;
import com.client.ws.rasmooplus.utils.RecoveryCodeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserRecoveryCodeServiceImpl implements UserRecoveryCodeService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    private final UserCredentialsRepository userCredentialsRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailIntegration mailIntegration;

    public UserRecoveryCodeServiceImpl(
            UserCredentialsRepository userCredentialsRepository,
            RedisTemplate<String, String> redisTemplate,
            MailIntegration mailIntegration) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.redisTemplate = redisTemplate;
        this.mailIntegration = mailIntegration;
    }

    @Override
    public void sendRecoveryCode(String email) {
        // 1. Verifica se o usuário existe pelo e-mail (username = email)
        userCredentialsRepository.findByUsername(email)
                .orElseThrow(() -> new NotFoundException(
                        "Usuário não encontrado com o e-mail: " + email));

        // 2. Gera o código de 4 dígitos
        String code = RecoveryCodeUtils.generate4DigitsCode();

        // 3. Salva no Redis: chave = "recovery-code:{email}", valor = código
        String redisKey = RecoveryCodeUtils.REDIS_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, code, CODE_TTL);

        // 4. Envia o código por e-mail
        String body = "Seu código de recuperação de senha é: " + code
                + "\n\nEste código é válido por 5 minutos.";
        mailIntegration.send(email, body, "Código de Recuperação - Rasmoo Plus");
    }
}