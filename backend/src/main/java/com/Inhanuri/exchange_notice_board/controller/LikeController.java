package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.LikeRequestDTO;
import com.Inhanuri.exchange_notice_board.dto.LikeResponseDTO;
import com.Inhanuri.exchange_notice_board.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponseDTO> likeCommunityPost(@RequestBody LikeRequestDTO likeRequestDTO, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(likeService.likeObject(likeRequestDTO, request));
    }
}
