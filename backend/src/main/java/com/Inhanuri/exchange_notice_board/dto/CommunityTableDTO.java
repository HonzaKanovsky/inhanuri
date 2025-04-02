package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommunityTableDTO {
    private Long id;
    private String title;
    private LocalDateTime updatedAt;
    private TagType tag;
    private int views = 0;
    private int likes = 0;
    private int commentCount = 0;
}
