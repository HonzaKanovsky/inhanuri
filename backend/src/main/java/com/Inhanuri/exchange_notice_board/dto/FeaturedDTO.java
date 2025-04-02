package com.Inhanuri.exchange_notice_board.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FeaturedDTO {
    List<FeaturedItemDTO> notices = new ArrayList<>();
    List<FeaturedItemDTO> communities = new ArrayList<>();
    List<FeaturedItemDTO> questions = new ArrayList<>();
}
