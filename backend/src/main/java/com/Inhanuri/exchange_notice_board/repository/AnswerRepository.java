package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.model.QAAnswer;
import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<QAAnswer,Long> {
}
