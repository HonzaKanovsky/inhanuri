package com.Inhanuri.exchange_notice_board.dto;


import com.Inhanuri.exchange_notice_board.enums.LikeType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class FeaturedItemDTO {
    private Long id;
    private String title;
    private Integer likes;
}
