package com.Inhanuri.exchange_notice_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    String username;
    String token;
}
