package com.Inhanuri.exchange_notice_board.dto;

import lombok.Data;

@Data
public class FileDTO {
    String fileName;
    String fileType;
    byte[] fileData;
}
