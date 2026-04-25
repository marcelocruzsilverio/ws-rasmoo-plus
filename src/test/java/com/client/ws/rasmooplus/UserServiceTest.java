package com.client.ws.rasmooplus;

import com.client.ws.rasmooplus.dto.UserDto;
import com.client.ws.rasmooplus.exception.BadRequestException;
import com.client.ws.rasmooplus.exception.NotFoundException;
import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.UserRepository;
import com.client.ws.rasmooplus.repository.UserTypeRepository;
import com.client.ws.rasmooplus.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // ── Objetos compartilhados ────────────────────────────────────────────────

    private UserDto userDto;
    private UserType userType;
    private User user;

    /**
     * Executado antes de CADA teste.
     * userDto.getId() parte como null — cada teste ajusta conforme necessário.
     */
    @BeforeEach
    void setUp() {
        userType = UserType.builder()
                .id(3L)
                .name("ALUNO")
                .description("Aluno da plataforma")
                .build();

        userDto = UserDto.builder()
                .name("Felipe Teste")
                .email("felipe@email.com")
                .phone("11999999999")
                .cpf("123.456.789-09")
                .dtSubscription(LocalDate.now())
                .dtExpiration(LocalDate.now().plusMonths(1))
                .userTypeId(3L)
                .build();
        // id permanece null por padrão

        user = User.builder()
                .id(1L)
                .name("Felipe Teste")
                .email("felipe@email.com")
                .phone("11999999999")
                .cpf("123.456.789-09")
                .dtSubscription(LocalDate.now())
                .dtExpiration(LocalDate.now().plusMonths(1))
                .userType(userType)
                .subscriptionType(null)
                .build();
    }

    // ── ✅ Cenário 1: Sucesso — ID nulo, UserType encontrado ──────────────────

    @Test
    void givenCreateUser_whenIdIsNull_andUserTypeFound_thenReturnCreatedUser() {
        // Arrange
        when(userTypeRepository.findById(3L)).thenReturn(Optional.of(userType));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(userType, result.getUserType());

        verify(userTypeRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ── ❌ Cenário 2: Exceção — ID não nulo → BadRequestException ─────────────

    @Test
    void givenCreateUser_whenIdIsNotNull_thenThrowBadRequestException() {
        // Arrange — simula requisição inválida com ID preenchido
        userDto.setId(10L);

        // Act & Assert
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.createUser(userDto)
        );

        assertEquals("User Id needs to be null", ex.getMessage());

        // Garante que nenhum repositório foi consultado
        verify(userTypeRepository, times(0)).findById(any());
        verify(userRepository, times(0)).save(any());
    }

    // ── ❌ Cenário 3: Exceção — UserType não encontrado → NotFoundException ───

    @Test
    void givenCreateUser_whenIdIsNull_andUserTypeNotFound_thenThrowNotFoundException() {
        // Arrange — repositório retorna Optional vazio
        when(userTypeRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userService.createUser(userDto)
        );

        assertEquals("UserTypeId not found", ex.getMessage());

        // UserType deve ter sido consultado exatamente 1 vez
        verify(userTypeRepository, times(1)).findById(3L);
        // save nunca deve ser chamado
        verify(userRepository, times(0)).save(any());
    }

    // ── ✅ Cenário extra: Usuário criado sem subscriptionType (null) ──────────

    @Test
    void givenCreateUser_whenValidDto_thenUserIsSavedWithNullSubscriptionType() {
        // Arrange
        when(userTypeRepository.findById(3L)).thenReturn(Optional.of(userType));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(userDto);

        // Assert — subscriptionType deve ser null na criação
        assertNull(result.getSubscriptionType());
        verify(userRepository, times(1)).save(any(User.class));
    }
}