package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.FileDTO;
import com.Inhanuri.exchange_notice_board.dto.FileResponseDTO;
import com.Inhanuri.exchange_notice_board.model.FileData;
import com.Inhanuri.exchange_notice_board.model.Notice;
import com.Inhanuri.exchange_notice_board.repository.FileRepository;
import com.Inhanuri.exchange_notice_board.repository.NoticeRepository;
import com.Inhanuri.exchange_notice_board.util.FileUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UploadService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    public FileResponseDTO uploadFile(MultipartFile file) throws IOException {

        FileData newFile = new FileData();
        newFile.setName(file.getOriginalFilename());
        newFile.setType(file.getContentType());
        newFile.setData(FileUtils.compressFile(file.getBytes()));
        fileRepository.save(newFile);

        return newFile.toDTO();
    }

    public FileDTO downloadFile(Long id) throws IOException {
        Optional<FileData> fileData = fileRepository.findById(id);
        FileDTO fileDTO = new FileDTO();

        fileData.ifPresent(file -> {
            fileDTO.setFileName(file.getName());
            fileDTO.setFileType(file.getType());
            fileDTO.setFileData(FileUtils.decompressFile(file.getData()));
        });

        if(fileData.isPresent()){
            return fileDTO;
        }
        return null;
    }

    @Transactional
    public String deleteFile(Long fileID) {
        Optional<FileData> fileDataOpt = fileRepository.findById(fileID);

        if (fileDataOpt.isPresent()) {
            FileData fileData = fileDataOpt.get();

            // Find all notices linked to this file
            List<Notice> notices = noticeRepository.findByFilesId(fileID);

            // Remove the file from the linked notices
            for (Notice notice : notices) {
                // Detach the file from the notice
                notice.getFiles().remove(fileData);
                // Save the notice after updating the file list
                noticeRepository.save(notice);
            }

            // Now delete the file
            fileRepository.deleteById(fileID);
        }

        return fileID.toString();
    }
}
