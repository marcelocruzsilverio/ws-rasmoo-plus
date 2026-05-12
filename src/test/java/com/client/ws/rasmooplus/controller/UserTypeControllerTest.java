package com.client.ws.rasmooplus.controller;

import com.client.ws.rasmooplus.model.UserType;
import com.client.ws.rasmooplus.service.TokenJwtService;
import com.client.ws.rasmooplus.service.UserTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTypeController.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserTypeService userTypeService;

    // O JwtAuthFilter agora injeta as interfaces (após a correção no código de produção).
    // Mesmo com addFilters=false, o Spring precisa instanciar o JwtAuthFilter para
    // registrá-lo no contexto, e por isso precisa desses beans disponíveis.
    @MockitoBean
    private TokenJwtService tokenJwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserType userType1;
    private UserType userType2;

    @BeforeEach
    void setUp() {
        userType1 = UserType.builder()
                .id(1L)
                .name("PROFESSOR")
                .description("Professores da plataforma - cadastro administrativo")
                .build();

        userType2 = UserType.builder()
                .id(2L)
                .name("ALUNO")
                .description("Aluno da plataforma - cadastro via fluxo normal")
                .build();
    }

    @Test
    void givenFindAll_whenUserTypesExist_thenReturnOkWithList() throws Exception {
        when(userTypeService.findAll()).thenReturn(List.of(userType1, userType2));

        mockMvc.perform(get("/user-type")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("PROFESSOR"))
                .andExpect(jsonPath("$[0].description").value("Professores da plataforma - cadastro administrativo"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("ALUNO"));

        verify(userTypeService, times(1)).findAll();
    }

    @Test
    void givenFindAll_whenNoUserTypesExist_thenReturnOkWithEmptyList() throws Exception {
        when(userTypeService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user-type")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userTypeService, times(1)).findAll();
    }
}