package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private int views = 0;
    private TagType tag;
    private Integer likes = 0;
    private List<AnswerDTO> answers;
}
