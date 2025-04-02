package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.dto.NoticesTableItemDTO;
import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Notice findNoticeById(long id);

    List<Notice> findTop5ByOrderByViewsDesc();

    Page<Notice> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Notice> findByTag(TagType tag, Pageable pageable);

    List<Notice> findByFilesId(Long fileId);

}
