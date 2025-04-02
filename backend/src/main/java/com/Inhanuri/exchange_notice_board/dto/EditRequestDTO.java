package com.Inhanuri.exchange_notice_board.dto;

import lombok.Data;

@Data
public class EditRequestDTO extends CommunityRequestDTO {
    private Long id;
    private String token;
}
