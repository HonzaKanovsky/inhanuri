package com.Inhanuri.exchange_notice_board.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    private final Map<String, TokenInfo> verificationTokens = new ConcurrentHashMap<>();

    // ✅ Helper class to store token details
    private static class TokenInfo {
        Long questionId;
        Instant expirationTime;

        TokenInfo(Long questionId, Instant expirationTime) {
            this.questionId = questionId;
            this.expirationTime = expirationTime;
        }
    }

    // ✅ Generate a token tied to a specific question ID
    public String generateToken(Long questionId) {
        String token = UUID.randomUUID().toString();
        verificationTokens.put(token, new TokenInfo(questionId, Instant.now().plusSeconds(300))); // Expire in 5 min
        return token;
    }

    // ✅ Validate token and ensure it belongs to the correct question
    public boolean isTokenValid(String token, Long questionId) {
        TokenInfo tokenInfo = verificationTokens.get(token);
        return tokenInfo != null && tokenInfo.questionId.equals(questionId) && Instant.now().isBefore(tokenInfo.expirationTime);
    }

    // ✅ Remove the token after use
    public void invalidateToken(String token) {
        verificationTokens.remove(token);
    }
}
