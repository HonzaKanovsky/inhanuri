package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.model.CommunityNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityNoticeRepository extends JpaRepository<CommunityNotice, Long> {
    List<CommunityNotice> findTop5ByOrderByLikesDescViewsDesc();

    Page<CommunityNotice> findByTag(TagType tagType, Pageable pageable);

    Page<CommunityNotice> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
