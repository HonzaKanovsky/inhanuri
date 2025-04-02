package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.LikeRequestDTO;
import com.Inhanuri.exchange_notice_board.dto.LikeResponseDTO;
import com.Inhanuri.exchange_notice_board.enums.LikeTargetType;
import com.Inhanuri.exchange_notice_board.enums.LikeType;
import com.Inhanuri.exchange_notice_board.exception.RepeatedLikeException;
import com.Inhanuri.exchange_notice_board.model.*;
import com.Inhanuri.exchange_notice_board.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRecordRepository likeRecordRepository;
    @Autowired
    private CommunityNoticeRepository communityNoticeRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private QARepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    public LikeResponseDTO likeObject(LikeRequestDTO likeRequestDTO, HttpServletRequest request) throws NoSuchAlgorithmException {
        String userIP = getClientIP(request);
        String hashedIp = HashUtil.hashIP(userIP);

        Object targetEntity = getTargetEntity(likeRequestDTO.getTargetType(), likeRequestDTO.getId());
        if (targetEntity == null) {
            throw new NoSuchElementException("Object with this id was not found");
        }

        Optional<LikeRecord> likeRecord = doesSameLikeRecordExist(likeRequestDTO, targetEntity, hashedIp);
        LikeType previousLikeType = LikeType.NEUTRAL;

        if (likeRecord.isPresent()) {
            LikeRecord existingLikeRecord = likeRecord.get();
            previousLikeType =  existingLikeRecord.getLikeType();
            existingLikeRecord.setLikedAt(Instant.now());
            existingLikeRecord.setLikeType(likeRequestDTO.getLikeType());
        } else {
            LikeRecord like = new LikeRecord();
            like.setHashedIp(hashedIp);
            like.setLikedAt(Instant.now());
            like.setLikeType(likeRequestDTO.getLikeType());
            like.setConnectionToObject(targetEntity, likeRequestDTO.getTargetType());
            likeRecordRepository.save(like);
        }

        updateLikeCount(targetEntity, previousLikeType, likeRequestDTO.getLikeType());

        LikeResponseDTO likeResponseDTO = new LikeResponseDTO();
        likeResponseDTO.setId(likeRequestDTO.getId());
        likeResponseDTO.setLikes(getLikeCount(targetEntity));

        return likeResponseDTO;
    }

    private Object getTargetEntity(LikeTargetType targetType, Long targetId) {
        return switch (targetType) {
            case NOTICE -> communityNoticeRepository.findById(targetId).orElse(null);
            case COMMENT -> commentRepository.findById(targetId).orElse(null);
            case QUESTION -> questionRepository.findById(targetId).orElse(null);
            case ANSWER -> answerRepository.findById(targetId).orElse(null);
        };
    }

    private Optional<LikeRecord> doesSameLikeRecordExist(LikeRequestDTO likeRequestDTO, Object targetEntity, String hashedIp) {
        Optional<LikeRecord> existingLike = switch (likeRequestDTO.getTargetType()) {
            case NOTICE -> likeRecordRepository.findByCommunityNoticeAndHashedIp((CommunityNotice) targetEntity, hashedIp);
            case COMMENT -> likeRecordRepository.findByCommentAndHashedIp((Comment) targetEntity, hashedIp);
            case QUESTION -> likeRecordRepository.findByQuestionAndHashedIp((QAQuestion) targetEntity, hashedIp);
            case ANSWER -> likeRecordRepository.findByAnswerAndHashedIp((QAAnswer) targetEntity, hashedIp);
        };

        if (existingLike.isPresent() && existingLike.get().getLikeType() == likeRequestDTO.getLikeType()) {
            throw new RepeatedLikeException("You have already liked this post today.");
        }

        return existingLike;
    }

    private void updateLikeCount(Object targetEntity, LikeType previousType, LikeType newType) {
        int likeChange = getLikeChange(previousType, newType);

        if (targetEntity instanceof CommunityNotice notice) {
            notice.setLikes(notice.getLikes() + likeChange);
            communityNoticeRepository.save(notice);
        } else if (targetEntity instanceof Comment comment) {
            comment.setLikes(comment.getLikes() + likeChange);
            commentRepository.save(comment);
        } else if (targetEntity instanceof QAQuestion question) {
            question.setLikes(question.getLikes() + likeChange);
            questionRepository.save(question);
        } else if (targetEntity instanceof QAAnswer answer) {
            answer.setLikes(answer.getLikes() + likeChange);
            answerRepository.save(answer);
        }
    }

    private static int getLikeChange(LikeType previousType, LikeType newType) {
        int likeChange = 0;

        if (previousType == LikeType.LIKE && newType == LikeType.NEUTRAL) {
            likeChange = -1;  // Remove a like
        } else if (previousType == LikeType.DISLIKE && newType == LikeType.NEUTRAL) {
            likeChange = 1;   // Remove a dislike (add a like)
        } else if (previousType == LikeType.NEUTRAL && newType == LikeType.LIKE) {
            likeChange = 1;   // Add a like
        } else if (previousType == LikeType.NEUTRAL && newType == LikeType.DISLIKE) {
            likeChange = -1;  // Add a dislike (remove a like)
        } else if (previousType == LikeType.LIKE && newType == LikeType.DISLIKE) {
            likeChange = -2;  // Remove a like and add a dislike
        } else if (previousType == LikeType.DISLIKE && newType == LikeType.LIKE) {
            likeChange = 2;   // Remove a dislike and add a like
        }
        return likeChange;
    }

    private int getLikeCount(Object targetEntity) {
        if (targetEntity instanceof CommunityNotice notice) {
            return notice.getLikes();
        } else if (targetEntity instanceof Comment comment) {
            return comment.getLikes();
        } else if (targetEntity instanceof QAQuestion question) {
            return question.getLikes();
        } else if (targetEntity instanceof QAAnswer answer) {
            return answer.getLikes();
        }
        return 0;
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}

