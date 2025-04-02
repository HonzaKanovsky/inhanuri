package com.Inhanuri.exchange_notice_board.dto;


import com.Inhanuri.exchange_notice_board.enums.LikeTargetType;
import com.Inhanuri.exchange_notice_board.enums.LikeType;
import lombok.Data;

@Data
public class LikeRequestDTO {
    private Long id;
    private LikeTargetType targetType;
    private LikeType likeType;
}
