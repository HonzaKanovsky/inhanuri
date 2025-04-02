package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.model.Comment;
import com.Inhanuri.exchange_notice_board.model.QAAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>  {
    void deleteByPostId(Long postId);
}
