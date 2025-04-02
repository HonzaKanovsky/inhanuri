package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.FileResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    @Lob
    @JsonIgnore
    private byte[] data;


    public FileResponseDTO toDTO(){
        return new FileResponseDTO(
                this.id,
                this.name
        );
    }
}
