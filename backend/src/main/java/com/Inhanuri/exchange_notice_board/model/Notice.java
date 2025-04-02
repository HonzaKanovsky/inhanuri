package com.Inhanuri.exchange_notice_board.model;

import com.Inhanuri.exchange_notice_board.dto.NoticeDTO;
import com.Inhanuri.exchange_notice_board.dto.NoticesTableItemDTO;
import com.Inhanuri.exchange_notice_board.enums.TagType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private TagType tag;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer views = 0;

    @OneToMany
    @JoinTable(
            name = "notice_files",
            joinColumns = @JoinColumn(name = "notice_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileData> files = new ArrayList<>();


    public Notice(String title, String content, TagType tag, List<FileData> files) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.files = files;
    }

    public NoticeDTO toDto(){
        return new NoticeDTO(
                id,
                title,
                content,
                createdAt,
                updatedAt,
                files.stream().map(FileData::toDTO).toList(),
                views
        );
    }

    public NoticesTableItemDTO toTableDto(){
        return new NoticesTableItemDTO(
                id,
                title,
                updatedAt,
                tag,
                views
        );
    }
}
