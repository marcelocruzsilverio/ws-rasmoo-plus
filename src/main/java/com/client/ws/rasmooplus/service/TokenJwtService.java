package com.client.ws.rasmooplus.service;

public interface TokenJwtService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token);
}