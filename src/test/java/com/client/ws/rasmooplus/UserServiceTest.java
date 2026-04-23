package com.client.ws.rasmooplus;

import com.client.ws.rasmooplus.model.User;
import com.client.ws.rasmooplus.dto.UserDto;
import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.UserRepository;
import com.client.ws.rasmooplus.repository.UserTypeRepository;
import com.client.ws.rasmooplus.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserTypeRepository userTypeRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenCreate_whenIdIsNotNullAndUserIsFound_thenReturnUserCreated() {
        UserDto dto = new UserDto();
        dto.setEmail("felipe@email.com");
        dto.setCpf("12345678901");
        dto.setUserTypeId(1L);

        UserType userType = new UserType(1L, "Aluno", "Aluno da plataforma");

        User expectedUser = new User();
        expectedUser.setEmail(dto.getEmail());
        expectedUser.setCpf(dto.getCpf());
        expectedUser.setUserType(userType);
        expectedUser.setDtSubscription(dto.getDtSubscription());
        expectedUser.setDtExpiration(dto.getDtExpiration());

        when(userTypeRepository.findById(1L))
                .thenReturn(Optional.of(userType));

        when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);

        User result = userService.createUser(dto);

        assertEquals(expectedUser, result);
        verify(userTypeRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(expectedUser);
    }
}
