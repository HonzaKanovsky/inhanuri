package com.Inhanuri.exchange_notice_board.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeUpdateRequestDTO extends NoticeRequestDTO {
    private Long id;
}
