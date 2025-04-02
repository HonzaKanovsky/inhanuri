package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.NoticeDTO;
import com.Inhanuri.exchange_notice_board.dto.QuestionDTO;
import com.Inhanuri.exchange_notice_board.dto.QuestionsTableItemDTO;
import com.Inhanuri.exchange_notice_board.enums.TagType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "qa_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QAQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private Integer likes = 0;

    @Enumerated(EnumType.STRING)
    @Column
    private TagType tag;

    @Column(nullable = false)
    private String passwordHash; // Used for anonymous post deletion

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isSolved DESC, isAdmin DESC,likes DESC, createdAt DESC")
    private List<QAAnswer> answers;

    public void setPassword(String rawPassword) {
        this.passwordHash = new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean checkPassword(String inputPassword) {
        return new BCryptPasswordEncoder().matches(inputPassword, this.passwordHash);
    }

    public QuestionDTO toDto(){
        return new QuestionDTO(
                id,
                title,
                content,
                createdAt,
                updatedAt,
                views,
                tag,
                likes,
                answers != null ? answers.stream().map(QAAnswer::toDto).collect(Collectors.toList()) : List.of()
        );
    }

    public QuestionsTableItemDTO toTableDto(){
        return new QuestionsTableItemDTO(
                id,
                title,
                updatedAt,
                views,
                tag,
                answers.size()
        );
    }
}