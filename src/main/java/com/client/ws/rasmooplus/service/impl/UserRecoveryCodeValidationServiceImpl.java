package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.service.UserRecoveryCodeValidationService;
import com.client.ws.rasmooplus.utils.RecoveryCodeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserRecoveryCodeValidationServiceImpl
        implements UserRecoveryCodeValidationService {

    private final RedisTemplate<String, String> redisTemplate;

    public UserRecoveryCodeValidationServiceImpl(
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean validateRecoveryCode(String email, String code) {
        String redisKey = RecoveryCodeUtils.REDIS_KEY_PREFIX + email;

        // Verifica o tempo de vida restante da chave no Redis
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);

        // Se TTL for -2, a chave não existe; se for -1, não tem expiração definida
        if (ttl == null || ttl <= 0) {
            return false;
        }

        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            return false;
        }

        return storedCode.equals(code);
    }
}