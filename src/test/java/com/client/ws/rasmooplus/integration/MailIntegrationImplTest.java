package com.client.ws.rasmooplus.integration;

import com.client.ws.rasmooplus.integration.impl.MailIntegrationImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link MailIntegrationImpl}.
 *
 * <p>Cenários cobertos:
 * <ul>
 *   <li>✅ Sucesso — {@code mailSender.send()} é chamado com os dados corretos</li>
 *   <li>❌ Exceção — {@code mailSender.send()} lança {@link MailSendException} e a exceção propaga</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class MailIntegrationImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailIntegrationImpl mailIntegration;

    private static final String MAIL_TO  = "usuario@email.com";
    private static final String MESSAGE  = "Seu código de recuperação é: 123456";
    private static final String SUBJECT  = "Recuperação de senha - Rasmoo Plus";

    // ══════════════════════════════════════════════════════════════════════════
    // send
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * ✅ Sucesso: verifica que o SimpleMailMessage é montado corretamente
     * e que {@code mailSender.send()} é chamado exatamente uma vez.
     *
     * <p>Usamos {@link ArgumentCaptor} para capturar o objeto enviado ao mailSender
     * e inspecionar seus campos, garantindo que o mapeamento está correto.
     */
    @Test
    void givenSend_whenValidArgs_thenMailSenderIsCalledWithCorrectMessage() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        mailIntegration.send(MAIL_TO, MESSAGE, SUBJECT);

        // Assert — verifica que send() foi chamado 1x e captura o argumento
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage);
        assertNotNull(sentMessage.getTo());
        assertEquals(1, sentMessage.getTo().length, "Deve haver exatamente 1 destinatário");
        assertEquals(MAIL_TO,  sentMessage.getTo()[0], "Destinatário deve ser o informado");
        assertEquals(SUBJECT,  sentMessage.getSubject(), "Assunto deve ser o informado");
        assertEquals(MESSAGE,  sentMessage.getText(), "Texto deve ser a mensagem informada");
    }

    /**
     * ❌ Exceção: {@code mailSender.send()} lança {@link MailSendException}.
     * A exceção deve propagar sem ser engolida pela implementação.
     */
    @Test
    void givenSend_whenMailSenderThrowsException_thenExceptionPropagates() {
        // Arrange
        doThrow(new MailSendException("Servidor SMTP indisponível"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        MailSendException ex = assertThrows(
                MailSendException.class,
                () -> mailIntegration.send(MAIL_TO, MESSAGE, SUBJECT)
        );

        assertTrue(ex.getMessage().contains("SMTP"),
                "A mensagem de erro deve referenciar o problema SMTP");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}