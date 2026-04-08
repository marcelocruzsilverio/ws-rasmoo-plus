package com.client.ws.rasmooplus.service.impl;

import com.client.ws.rasmooplus.dto.LoginDto;
import com.client.ws.rasmooplus.dto.TokenDto;
import com.client.ws.rasmooplus.service.AuthenticationService;
import com.client.ws.rasmooplus.service.TokenJwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenJwtService tokenJwtService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, TokenJwtService tokenJwtService) {
        this.authenticationManager = authenticationManager;
        this.tokenJwtService = tokenJwtService;
    }

    @Override
    public TokenDto auth(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        String token = tokenJwtService.generateToken(loginDto.getUsername());
        return new TokenDto(token, "Bearer");
    }
}