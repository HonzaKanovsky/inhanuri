package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.FeaturedDTO;
import com.Inhanuri.exchange_notice_board.dto.FeaturedItemDTO;
import com.Inhanuri.exchange_notice_board.model.CommunityNotice;
import com.Inhanuri.exchange_notice_board.model.Notice;
import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import com.Inhanuri.exchange_notice_board.repository.CommunityNoticeRepository;
import com.Inhanuri.exchange_notice_board.repository.NoticeRepository;
import com.Inhanuri.exchange_notice_board.repository.QARepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeaturedService {
    @Autowired
    CommunityNoticeRepository communityNoticeRepository;
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    QARepository qaRepository;

    public FeaturedDTO getFeaturedItems() {
        FeaturedDTO featuredDTO = new FeaturedDTO();

        List<Notice> databaseNotices = noticeRepository.findTop5ByOrderByViewsDesc();
        for (Notice notice : databaseNotices) {
            featuredDTO.getNotices().add(new FeaturedItemDTO(
                    notice.getId(),
                    notice.getTitle(),
                    null
            ));
        }
        List<CommunityNotice> databaseCommunityNotices = communityNoticeRepository.findTop5ByOrderByLikesDescViewsDesc();
        for (CommunityNotice communityNotice : databaseCommunityNotices) {
            featuredDTO.getCommunities().add(new FeaturedItemDTO(
                    communityNotice.getId(),
                    communityNotice.getTitle(),
                    communityNotice.getLikes()
            ));
        }
        List<QAQuestion> databaseQuestions = qaRepository.findTop5ByOrderByLikesDescViewsDesc();
        for (QAQuestion question : databaseQuestions) {
            featuredDTO.getQuestions().add(new FeaturedItemDTO(
                    question.getId(),
                    question.getTitle(),
                    question.getLikes()
            ));
        }

        return featuredDTO;
    }
}
