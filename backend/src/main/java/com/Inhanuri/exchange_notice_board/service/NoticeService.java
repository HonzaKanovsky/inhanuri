package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.NoticeDTO;
import com.Inhanuri.exchange_notice_board.dto.NoticeRequestDTO;
import com.Inhanuri.exchange_notice_board.dto.NoticeUpdateRequestDTO;
import com.Inhanuri.exchange_notice_board.dto.NoticesTableItemDTO;
import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.exception.IncompleteRequestException;
import com.Inhanuri.exchange_notice_board.model.FileData;
import com.Inhanuri.exchange_notice_board.model.Notice;
import com.Inhanuri.exchange_notice_board.repository.FileRepository;
import com.Inhanuri.exchange_notice_board.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private FileRepository fileRepository;

    public NoticeDTO createNotice(String authHeader, NoticeRequestDTO noticeRequestDto){
        doCommonNoticeValidations(authHeader, noticeRequestDto);
        List<FileData> files = new ArrayList<>();

        if(noticeRequestDto.getFileIds().isPresent()){
            files = getFilesByIds(noticeRequestDto.getFileIds().get());
        }

        Notice notice = new Notice(noticeRequestDto.getTitle(), noticeRequestDto.getContent(), noticeRequestDto.getTag(), files);
        return noticeRepository.save(notice).toDto();
    }

    private List<FileData> getFilesByIds(List<Long> ids){
        return fileRepository.findAllById(ids);
    }

    public Page<NoticesTableItemDTO> getAllNotices(Pageable pageable){
        return noticeRepository.findAll(pageable).map(Notice::toTableDto);
    }

    @Transactional
    public NoticeDTO getNotice(Long id, Boolean isFirstVisit) {
        Notice notice = noticeRepository.findNoticeById(id);

        if(notice == null)
            throw new NoSuchElementException("Notice with this id was not found");

        if(isFirstVisit){
            notice.setViews(notice.getViews() + 1);
            noticeRepository.save(notice);
        }

        return notice.toDto();
    }

    public void deleteNotice(String authHeader, Long id) {
            authService.validateToken(authHeader);

            Notice notice = noticeRepository.findNoticeById(id);

            if(notice == null)
                throw new NoSuchElementException("Notice with this id was not found");

            noticeRepository.delete(notice);
    }

    @Transactional
    public NoticeDTO updateNotice(String authHeader, NoticeUpdateRequestDTO noticeRequestDto) {
        doCommonNoticeValidations(authHeader, noticeRequestDto);

        if(noticeRequestDto.getId() == null)
            throw new IncompleteRequestException("Id is missing");

        Notice notice = noticeRepository.findNoticeById(noticeRequestDto.getId());

        if(notice == null)
            throw new NoSuchElementException("Notice with this id was not found");

        List<FileData> files = new ArrayList<>();

        if(noticeRequestDto.getFileIds().isPresent()){
            files = getFilesByIds(noticeRequestDto.getFileIds().get());
        }


        notice.setTitle(noticeRequestDto.getTitle());
        notice.setContent(noticeRequestDto.getContent());
        notice.setUpdatedAt(LocalDateTime.now());
        notice.setFiles(files);

        return noticeRepository.save(notice).toDto();
    }

    private void doCommonNoticeValidations(String authHeader, NoticeRequestDTO noticeRequestDto){
        doesNoticeRequestDTOHaveRequiredFields(noticeRequestDto);
        authService.validateToken(authHeader);
    }

    private void doesNoticeRequestDTOHaveRequiredFields(NoticeRequestDTO noticeRequestDto){
        if (!StringUtils.hasLength(noticeRequestDto.getTitle()) || !StringUtils.hasLength(noticeRequestDto.getContent()))
            throw new IncompleteRequestException("Required fields are missing");
    }

    public Page<NoticesTableItemDTO> findByTitleContaining(String title, Pageable pageable) {
        return noticeRepository.findByTitleContainingIgnoreCase(title, pageable).map(Notice::toTableDto);
    }

    public Page<NoticesTableItemDTO> findByTag(String tag, Pageable pageable) {
        return noticeRepository.findByTag(TagType.valueOf(tag), pageable).map(Notice::toTableDto);
    }

}
