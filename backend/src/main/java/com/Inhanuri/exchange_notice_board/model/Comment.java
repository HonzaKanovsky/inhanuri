package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.AnswerDTO;
import com.Inhanuri.exchange_notice_board.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityNotice post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private Boolean isAdmin = false;

    @Column
    private Boolean isSolved = false;

    public CommentDTO toDto(){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(id);
        commentDTO.setLikes(likes);
        commentDTO.setContent(content);
        commentDTO.setCreatedAt(createdAt);
        commentDTO.setIsAdmin(isAdmin);
        commentDTO.setIsSolved(isSolved);

        return commentDTO;
    }
}