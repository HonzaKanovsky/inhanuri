package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QARepository extends JpaRepository<QAQuestion,Long> {
    List<QAQuestion> findTop5ByOrderByLikesDescViewsDesc();

    Page<QAQuestion> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<QAQuestion> findByTag(TagType tagType, Pageable pageable);
}
