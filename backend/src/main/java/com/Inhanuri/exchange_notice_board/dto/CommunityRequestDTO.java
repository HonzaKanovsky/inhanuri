package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.model.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommunityRequestDTO {
    private String title;
    private String content;
    private TagType tag;
    private String password;
}
