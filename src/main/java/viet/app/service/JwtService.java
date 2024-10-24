package viet.app.service;


import org.springframework.security.core.userdetails.UserDetails;
import viet.app.util.TokenType;

import java.util.List;

public interface JwtService {

    String generateToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String generateResetToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);

    List<String> extractRoles(String token, TokenType tokenType);
}
