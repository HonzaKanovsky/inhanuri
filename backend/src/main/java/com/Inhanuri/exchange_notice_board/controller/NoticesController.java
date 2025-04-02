package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.*;
import com.Inhanuri.exchange_notice_board.model.Notice;
import com.Inhanuri.exchange_notice_board.repository.NoticeRepository;
import com.Inhanuri.exchange_notice_board.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api/notice")
public class NoticesController {

    private static final String UPLOAD_DIR = "/uploads/files";

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeDTO> postNotice(@RequestHeader("Authorization") String authHeader, @RequestBody NoticeRequestDTO noticeRequestDto){
        NoticeDTO noticeDTO= noticeService.createNotice(authHeader, noticeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeDTO);
    }

     @PutMapping
     public ResponseEntity<NoticeDTO> patchNotice(@RequestHeader("Authorization") String authHeader, @RequestBody NoticeUpdateRequestDTO noticeRequestDto){
         NoticeDTO noticeDTO= noticeService.updateNotice(authHeader, noticeRequestDto);
         return ResponseEntity.status(HttpStatus.OK).body(noticeDTO);
     }

    @GetMapping
    public ResponseEntity<Page<NoticesTableItemDTO>> getNotices(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(noticeService.getAllNotices(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable Long id, @RequestParam(value = "isFirstVisit", defaultValue = "false") Boolean isFirstVisit) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(noticeService.getNotice(id,isFirstVisit));
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<NoticesTableItemDTO>> getFilteredNotices(
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "updatedAt") String sort,  // Sorting field
            @RequestParam(defaultValue = "desc") String order,      // Sorting order
            @RequestParam(defaultValue = "0") int page,             // Page number
            @RequestParam(defaultValue = "10") int size) {          // Page size

        // Determine sort direction
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Create pageable object with dynamic sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<NoticesTableItemDTO> result;


        if ("title".equalsIgnoreCase(filterBy) && query != null) {
            result = noticeService.findByTitleContaining(query, pageable);
        } else if ("tag".equalsIgnoreCase(filterBy) && query != null) {
            result = noticeService.findByTag(query, pageable);
        } else {
            result = noticeService.getAllNotices(pageable);
        }

        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try{
            noticeService.deleteNotice(authHeader, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
