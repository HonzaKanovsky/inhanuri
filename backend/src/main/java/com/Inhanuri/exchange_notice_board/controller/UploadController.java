package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.FileDTO;
import com.Inhanuri.exchange_notice_board.dto.FileResponseDTO;
import com.Inhanuri.exchange_notice_board.service.UploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.directory}") // Directory for file storage
    private String uploadDirectory;

    @Value("${upload.path}") // URL path for serving the uploaded files
    private String uploadPath;

    @Autowired
    private UploadService uploadService;

    // Upload a single image file
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a file."));
        }

        try {
            // Generate a unique filename using UUID
            String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

            // Create the upload directory if it does not exist
            Path uploadDir = Paths.get(uploadDirectory);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Define the target file path
            Path filePath = uploadDir.resolve(fileName);
            // Copy the file to the target path
            Files.copy(file.getInputStream(), filePath);

            // Construct the URL for the uploaded file
            String imageUrl = uploadPath + "/" + fileName;

            // Return the URL of the uploaded image
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload file."));
        }
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file) {
        try{
            FileResponseDTO uploadFile = uploadService.uploadFile(file);
            return ResponseEntity.ok().body(uploadFile);

        } catch (IOException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/files/{fileID}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileID) {

        try{
            FileDTO fileDTO = uploadService.downloadFile(fileID);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType(fileDTO.getFileType())).body(fileDTO.getFileData());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Upload multiple files
    @DeleteMapping("/files/{fileID}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileID) {

        return ResponseEntity.status(HttpStatus.OK).body(uploadService.deleteFile(fileID));
    }
}
