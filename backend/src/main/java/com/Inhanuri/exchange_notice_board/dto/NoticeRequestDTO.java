package com.Inhanuri.exchange_notice_board.dto;

import com.Inhanuri.exchange_notice_board.enums.TagType;
import lombok.Data;

import java.util.List;
import java.util.Optional;


@Data
public class NoticeRequestDTO {
    private String title;
    private String content;
    private TagType tag;
    private Boolean isFirstVisit;
    private Optional<List<Long>> fileIds;
}
