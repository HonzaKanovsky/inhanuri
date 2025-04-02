package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.LoginResponseDTO;
import com.Inhanuri.exchange_notice_board.dto.UserDTO;
import com.Inhanuri.exchange_notice_board.enums.RegexPatterns;
import com.Inhanuri.exchange_notice_board.exception.*;
import com.Inhanuri.exchange_notice_board.model.AdminUser;
import com.Inhanuri.exchange_notice_board.model.BlacklistedToken;
import com.Inhanuri.exchange_notice_board.repository.AdminUserRepository;
import com.Inhanuri.exchange_notice_board.repository.BlacklistTokenRepository;
import com.Inhanuri.exchange_notice_board.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Autowired
    BlacklistTokenRepository blacklistTokenRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    JwtUtil jwtUtil = new JwtUtil();

    public void register(UserDTO userDto) {
        if(!isUsernameValid(userDto.getUsername()))
            throw new InvalidPasswordException("Username does not comply with requirements.");

        if(!isPasswordValid(userDto.getPassword()))
            throw new InvalidUsernameException("Password does not comply with requirements.");

        if(adminUserRepository.findById(userDto.getUsername()).isPresent())
            throw new UserAlreadyExistsException("Username already exists.");

        AdminUser newUser = new AdminUser();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(userDto.getPassword());

        adminUserRepository.save(newUser);
    }

    public LoginResponseDTO login(UserDTO userDto) {
        if(adminUserRepository.findById(userDto.getUsername()).isEmpty())
            throw new UserDoesNotExistException("User does not exist.");

        String hashedPassword = adminUserRepository.findAdminUserByUsername(userDto.getUsername()).getPassword();
        if(!passwordEncoder.matches(userDto.getPassword(), hashedPassword))
            throw new InvalidPasswordException("Invalid password.");

        String accessToken = jwtUtil.generateToken(userDto.getUsername());

        return new LoginResponseDTO(userDto.getUsername(),accessToken);

    }

    public void logout(String token) {
        blacklistTokenRepository.save(new BlacklistedToken(token));
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }

    public void validateToken(String authHeader){

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new AuthorizationNotCorrectException("Invalid token format");

        String token = authHeader.substring(7);

        if (isTokenBlacklisted(token)) {
            throw new TokenValidationException("Token has been invalidated (Logged out)");
        }

        String username = jwtUtil.extractUsername(token);

        if (!jwtUtil.validateToken(token, username)) {
            throw new TokenValidationException("Invalid or expired token");
        }
    }

    private Boolean isUsernameValid(String username) {
        return RegexPatterns.USERNAME.matches(username);
    }
    private Boolean isPasswordValid(String password) {
        return RegexPatterns.PASSWORD.matches(password);
    }

}
