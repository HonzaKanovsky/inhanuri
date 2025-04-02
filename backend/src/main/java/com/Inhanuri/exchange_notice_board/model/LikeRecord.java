package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.enums.LikeTargetType;
import com.Inhanuri.exchange_notice_board.enums.LikeType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.tool.schema.TargetType;

import java.time.Instant;

import static com.Inhanuri.exchange_notice_board.enums.LikeTargetType.*;

@Data
@Entity
@Table(name = "like_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"notice_id", "hashed_ip","like_type"}),
        @UniqueConstraint(columnNames = {"comment_id", "hashed_ip","like_type"}),
        @UniqueConstraint(columnNames = {"question_id", "hashed_ip","like_type"}),
        @UniqueConstraint(columnNames = {"answer_id", "hashed_ip","like_type"})
})
public class LikeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private CommunityNotice communityNotice;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QAQuestion question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private QAAnswer answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "like_type", nullable = false)
    private LikeType likeType;

    @Column(name = "hashed_ip", nullable = false)
    private String hashedIp;

    @Column(name = "liked_at", nullable = false)
    private Instant likedAt;


    public void setConnectionToObject(Object entity, LikeTargetType targetType){
        switch (targetType) {
            case NOTICE -> communityNotice = (CommunityNotice) entity;
            case COMMENT -> comment = (Comment) entity;
            case QUESTION -> question = (QAQuestion) entity;
            case ANSWER -> answer = (QAAnswer) entity;
        }

    }
}