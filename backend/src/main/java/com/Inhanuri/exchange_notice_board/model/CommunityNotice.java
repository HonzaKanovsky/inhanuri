package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.CommunityDTO;
import com.Inhanuri.exchange_notice_board.dto.CommunityTableDTO;
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
@Table(name = "community_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private TagType tag;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int likes = 0;

    @Column(nullable = false)
    private String passwordHash; // Used for anonymous post deletion

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isSolved DESC, isAdmin DESC, likes DESC, createdAt DESC")
    private List<Comment> comments;


    public void setPassword(String rawPassword) {
        this.passwordHash = new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean checkPassword(String inputPassword) {
        return new BCryptPasswordEncoder().matches(inputPassword, this.passwordHash);
    }

    public CommunityDTO toDto(){
        return new CommunityDTO(
                id,
                title,
                content,
                tag,
                createdAt,
                updatedAt,
                views,
                likes,
                comments != null ? comments.stream().map(Comment::toDto).collect(Collectors.toList()) : List.of()

        );
    }

    public CommunityTableDTO toTableDto(){
        return new CommunityTableDTO(
                id,
                title,
                updatedAt,
                tag,
                views,
                likes,
                comments.size()
        );
    }
}