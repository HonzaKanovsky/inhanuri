package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import lombok.Data;

@Data
public class QuestionRequestDTO {
    private String title;
    private String content;
    private TagType tag;
    private String password;
}
