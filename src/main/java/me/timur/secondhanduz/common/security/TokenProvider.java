package me.timur.secondhanduz.common.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Port for JWT token operations — enables clean mocking in unit tests.
 */
public interface TokenProvider {

    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
