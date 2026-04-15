package com.client.ws.rasmooplus;

import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.repository.UserTypeRepository;
import com.client.ws.rasmooplus.service.UserTypeService;
import com.client.ws.rasmooplus.service.impl.UserTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTypeServiceTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeServiceImpl userTypeServiceImpl;

    @Test
    void shouldReturnAllUserTypes() {
        List<UserType> mockList = List.of(
                new UserType(1L, "PROFESSOR", "Professores da plataforma - cadastro administrativo"),
                new UserType(2L, "ADMINISTRADOR", "Administrado da plataforma - cadastro administrativo"),
                new UserType(3L, "ALUNO", "Aluno da plataforma - cadastro via fluxo normal")
        );

        when(userTypeRepository.findAll()).thenReturn(mockList);
        List<UserType> result = userTypeServiceImpl.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(userTypeRepository, times(1)).findAll();
    }
}