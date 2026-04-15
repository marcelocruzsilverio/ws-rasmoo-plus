package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.UserNewPasswordDto;
import com.client.ws.rasmooplus.exception.BadRequestException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.model.UserCredentials;
import com.client.ws.rasmooplus.repository.UserCredentialsRepository;
import com.client.ws.rasmooplus.service.UserPasswordUpdateService;
import com.client.ws.rasmooplus.service.UserRecoveryCodeValidationService;
import com.client.ws.rasmooplus.utils.RecoveryCodeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordUpdateServiceImpl implements UserPasswordUpdateService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRecoveryCodeValidationService validationService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordUpdateServiceImpl(
            UserCredentialsRepository userCredentialsRepository,
            UserRecoveryCodeValidationService validationService,
            RedisTemplate<String, String> redisTemplate,
            PasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.validationService = validationService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updatePassword(UserNewPasswordDto dto) {
        // 1. Re-valida o código (reutiliza o service da aula 4/6)
        Boolean isValid = validationService.validateRecoveryCode(
                dto.getEmail(), dto.getRecoveryCode());

        if (!isValid) {
            throw new BadRequestException(
                    "Código de recuperação inválido ou expirado");
        }

        // 2. Busca as credenciais do usuário
        UserCredentials userCredentials = userCredentialsRepository
                .findByUsername(dto.getEmail())
                .orElseThrow(() -> new NotFoundException(
                        "Usuário não encontrado com o e-mail: " + dto.getEmail()));

        // 3. Atualiza a senha com hash BCrypt
        userCredentials.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userCredentialsRepository.save(userCredentials);

        // 4. Remove o código do Redis — impede reuso do mesmo código
        redisTemplate.delete(RecoveryCodeUtils.REDIS_KEY_PREFIX + dto.getEmail());
    }
}
