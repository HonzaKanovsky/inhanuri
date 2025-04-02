package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.AnswerDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QAAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QAQuestion question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer likes = 0;

    @Column
    private Boolean isAdmin = false;

    @Column
    private Boolean isSolved = false;

    public AnswerDTO toDto(){
        AnswerDTO answerDto = new AnswerDTO();
        answerDto.setId(id);
        answerDto.setContent(content);
        answerDto.setCreatedAt(createdAt);
        answerDto.setIsAdmin(isAdmin);
        answerDto.setIsSolved(isSolved);
        answerDto.setLikes(likes);
        return answerDto;
    }
}