package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.EditRequestDTO;
import com.Inhanuri.exchange_notice_board.dto.LoginResponseDTO;
import com.Inhanuri.exchange_notice_board.service.AuthService;
import com.Inhanuri.exchange_notice_board.dto.UserDTO;
import com.Inhanuri.exchange_notice_board.service.CommunityService;
import com.Inhanuri.exchange_notice_board.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class  AdminUserController {

    @Autowired
    AuthService authService;

    @Autowired
    QAService qaService;

    @Autowired
    CommunityService communityService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerAdminUser(@RequestBody UserDTO userDto) {
        authService.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginAdminUser(@RequestBody UserDTO userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(userDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutAdminUser(@RequestHeader("Authorization") String authHeader) {
        authService.validateToken(authHeader);

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        authService.logout(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/questions")
    public ResponseEntity<String> deleteQuestion(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EditRequestDTO editRequestDTO) {

        authService.validateToken(authHeader);

        qaService.adminDeleteQuestion(editRequestDTO.getId());
        return ResponseEntity.ok("Question deleted successfully.");
    }

    @PutMapping("/questions")
    public ResponseEntity<String> updateQuestion(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EditRequestDTO editRequestDTO) {

        authService.validateToken(authHeader);
        qaService.adminEditQuestion(editRequestDTO);
        return ResponseEntity.ok("Question updated successfully.");
    }

    @DeleteMapping("/community")
    public ResponseEntity<String> deleteCommunity(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EditRequestDTO editRequestDTO) {

        authService.validateToken(authHeader);

        communityService.adminDeleteCommunity(editRequestDTO.getId());
        return ResponseEntity.ok("Question deleted successfully.");
    }

    @PutMapping("/community")
    public ResponseEntity<String> updateCommunity(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EditRequestDTO editRequestDTO) {

        authService.validateToken(authHeader);
        communityService.adminEditCommunity(editRequestDTO);
        return ResponseEntity.ok("Question updated successfully.");
    }


}
