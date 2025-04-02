package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerRequestDTO {
    private String content;
    private Boolean isAdmin;
}