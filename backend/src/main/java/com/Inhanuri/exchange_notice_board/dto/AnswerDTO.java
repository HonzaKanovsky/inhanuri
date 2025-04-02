package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private String content;
    private Integer likes;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Boolean isAdmin;
    private Boolean isSolved;
}
