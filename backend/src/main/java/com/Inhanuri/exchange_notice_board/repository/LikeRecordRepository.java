package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.enums.LikeType;
import com.Inhanuri.exchange_notice_board.model.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    Optional<LikeRecord> findByCommunityNoticeAndHashedIp(CommunityNotice communityNotice, String hashedIp);
    Optional<LikeRecord> findByCommentAndHashedIp(Comment comment, String hashedIp);
    Optional<LikeRecord> findByQuestionAndHashedIp(QAQuestion question, String hashedIp);
    Optional<LikeRecord> findByAnswerAndHashedIp(QAAnswer answer, String hashedIp);

    // Delete likes linked to the community notice
    void deleteByCommunityNotice(CommunityNotice communityNotice);

    void deleteByComment_Id(Long id);
    void deleteByAnswer_Id(Long id);

    // Delete likes linked to comments under a community notice
    @Modifying
    @Query("DELETE FROM LikeRecord lr WHERE lr.comment.id IN (SELECT c.id FROM Comment c WHERE c.post = :communityNotice)")
    void deleteByCommunityNoticeComments(@Param("communityNotice") CommunityNotice communityNotice);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeRecord lr WHERE lr.answer.id IN (SELECT a.id FROM QAAnswer a WHERE a.question = :question)")
    void deleteByQuestionAnswers(@Param("question") QAQuestion question);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeRecord lr WHERE lr.question = :question")
    void deleteByQuestion(@Param("question") QAQuestion question);


}