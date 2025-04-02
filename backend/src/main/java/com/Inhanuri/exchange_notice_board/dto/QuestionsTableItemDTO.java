package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionsTableItemDTO {
    private Long id;
    private String title;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private int views = 0;
    private TagType tag;
    private int answerCount = 0;
}
