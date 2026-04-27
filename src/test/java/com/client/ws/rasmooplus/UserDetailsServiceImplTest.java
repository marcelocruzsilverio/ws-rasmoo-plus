package com.client.ws.rasmooplus;

import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.integration.MailIntegration;
import com.client.ws.rasmooplus.model.UserCredentials;
import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.UserCredentialsRepository;
import com.client.ws.rasmooplus.service.impl.UserDetailsServiceImpl;
import com.client.ws.rasmooplus.service.impl.UserRecoveryCodeServiceImpl;
import com.client.ws.rasmooplus.utils.RecoveryCodeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes para UserDetailsServiceImpl  →  loadUserByUsername
 * Testes para UserRecoveryCodeServiceImpl →  sendRecoveryCode
 * <p>
 * Cobertura-alvo: 100% de linhas nos dois métodos.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    // ── Mocks compartilhados pelos dois services ──────────────────────────────

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private MailIntegration mailIntegration;

    // ── Services sob teste ────────────────────────────────────────────────────

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private UserRecoveryCodeServiceImpl userRecoveryCodeService;

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private static final String EMAIL = "felipe@email.com";
    private static final String REDIS_KEY = RecoveryCodeUtils.REDIS_KEY_PREFIX + EMAIL;

    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        UserType userType = UserType.builder()
                .id(3L)
                .name("ALUNO")
                .description("Aluno da plataforma")
                .build();

        userCredentials = new UserCredentials(
                1L,
                EMAIL,
                "$2a$10$hashedpassword",
                userType
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  loadUserByUsername
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Cenário de sucesso: usuário encontrado pelo username.
     * Linha coberta: return userCredentialsRepository.findByUsername(...)
     */
    @Test
    void givenLoadUserByUsername_whenUsernameExists_thenReturnUserDetails() {
        // Arrange
        when(userCredentialsRepository.findByUsername(EMAIL))
                .thenReturn(Optional.of(userCredentials));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        assertEquals(userCredentials.getPassword(), result.getPassword());

        verify(userCredentialsRepository, times(1)).findByUsername(EMAIL);
    }

    /**
     * ❌ Cenário de exceção: usuário NÃO encontrado.
     * Linha coberta: .orElseThrow(() -> new UsernameNotFoundException(...))
     */
    @Test
    void givenLoadUserByUsername_whenUsernameNotFound_thenThrowUsernameNotFoundException() {
        // Arrange
        when(userCredentialsRepository.findByUsername(EMAIL))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(EMAIL)
        );

        assertTrue(ex.getMessage().contains(EMAIL),
                "A mensagem de erro deve conter o username buscado");

        verify(userCredentialsRepository, times(1)).findByUsername(EMAIL);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  sendRecoveryCode  (UserRecoveryCodeServiceImpl)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Cenário de sucesso: usuário encontrado → gera código → salva no Redis → envia e-mail.
     * <p>
     * Linhas cobertas:
     * 1. userCredentialsRepository.findByUsername(email).orElseThrow(...)  → encontrado
     * 2. RecoveryCodeUtils.generate4DigitsCode()
     * 3. redisTemplate.opsForValue().set(redisKey, code, CODE_TTL)
     * 4. mailIntegration.send(email, body, subject)
     */
    @Test
    void givenSendRecoveryCode_whenUserExists_thenSaveCodeInRedisAndSendEmail() {
        // Arrange
        when(userCredentialsRepository.findByUsername(EMAIL))
                .thenReturn(Optional.of(userCredentials));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        userRecoveryCodeService.sendRecoveryCode(EMAIL);

        // Assert — Redis deve ter recebido a chave correta com TTL de 5 minutos
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);

        verify(valueOperations, times(1))
                .set(keyCaptor.capture(), valueCaptor.capture(), ttlCaptor.capture());

        assertEquals(REDIS_KEY, keyCaptor.getValue(),
                "A chave do Redis deve ser 'recovery-code:{email}'");

        String capturedCode = valueCaptor.getValue();
        assertNotNull(capturedCode);
        assertEquals(4, capturedCode.length(),
                "O código deve ter exatamente 4 dígitos");
        assertTrue(capturedCode.matches("\\d{4}"),
                "O código deve ser numérico");

        assertEquals(Duration.ofMinutes(5), ttlCaptor.getValue(),
                "O TTL deve ser de 5 minutos");

        // Assert — e-mail enviado para o endereço correto
        ArgumentCaptor<String> mailToCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailIntegration, times(1))
                .send(mailToCaptor.capture(), bodyCaptor.capture(), subjectCaptor.capture());

        assertEquals(EMAIL, mailToCaptor.getValue());
        assertTrue(bodyCaptor.getValue().contains(capturedCode),
                "O corpo do e-mail deve conter o código gerado");
        assertEquals("Código de Recuperação - Rasmoo Plus", subjectCaptor.getValue());

        verify(userCredentialsRepository, times(1)).findByUsername(EMAIL);
    }

    /**
     * ❌ Cenário de exceção: usuário NÃO encontrado → lança NotFoundException.
     * <p>
     * Linhas cobertas:
     * 1. userCredentialsRepository.findByUsername(email).orElseThrow(...)  → não encontrado
     * → nenhuma interação com Redis ou Mail deve ocorrer
     */
    @Test
    void givenSendRecoveryCode_whenUserNotFound_thenThrowNotFoundException() {
        // Arrange
        when(userCredentialsRepository.findByUsername(EMAIL))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userRecoveryCodeService.sendRecoveryCode(EMAIL)
        );

        assertTrue(ex.getMessage().contains(EMAIL),
                "A mensagem de erro deve identificar o e-mail não encontrado");

        // Redis e Mail NUNCA devem ser chamados
        verify(redisTemplate, times(0)).opsForValue();
        verify(mailIntegration, times(0)).send(any(), any(), any());
        verify(userCredentialsRepository, times(1)).findByUsername(EMAIL);
    }
}
