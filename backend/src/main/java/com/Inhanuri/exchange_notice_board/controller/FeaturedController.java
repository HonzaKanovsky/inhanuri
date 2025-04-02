package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.FeaturedDTO;
import com.Inhanuri.exchange_notice_board.service.FeaturedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/featured")
public class FeaturedController {
    @Autowired
    private FeaturedService featuredService;

    @GetMapping
    public ResponseEntity<FeaturedDTO> getFeaturedItems(){
        return ResponseEntity.ok().body(featuredService.getFeaturedItems());
    }
}
