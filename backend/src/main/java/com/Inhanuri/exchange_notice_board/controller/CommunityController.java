package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.*;
import com.Inhanuri.exchange_notice_board.service.AuthService;
import com.Inhanuri.exchange_notice_board.service.CommunityService;
import com.Inhanuri.exchange_notice_board.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    AuthService authService;

    @Autowired
    CommunityService communityService;

    @Autowired
    VerificationService verificationService;

    // get page
    @GetMapping
    public ResponseEntity<Page<CommunityTableDTO>> getCommunityPage(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(communityService.getCommunityPage(pageable));
    }

    // get single
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunityNotice(@PathVariable Long id, @RequestParam(value = "isFirstVisit", defaultValue = "false") Boolean isFirstVisit){
        return ResponseEntity.status(HttpStatus.OK).body(communityService.getCommunityNoticeByID(id, isFirstVisit));
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<CommunityTableDTO>> getFilteredNotices(
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "updatedAt") String sort,  // Sorting field
            @RequestParam(defaultValue = "desc") String order,      // Sorting order
            @RequestParam(defaultValue = "0") int page,             // Page number
            @RequestParam(defaultValue = "10") int size) {          // Page size

        // Determine sort direction
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Create pageable object with dynamic sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<CommunityTableDTO> result;

        if ("title".equalsIgnoreCase(filterBy) && query != null) {
            result = communityService.findByTitleContaining(query, pageable);
        } else if ("tag".equalsIgnoreCase(filterBy) && query != null) {
            result = communityService.findByTag(query, pageable);
        } else {
            result = communityService.getCommunityPage(pageable);
        }

        return ResponseEntity.ok(result);
    }


    // create
    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunityNotice(@RequestBody CommunityRequestDTO communityRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(communityService.saveCommunityNotice(communityRequestDTO));
    }

    // edit
    @PostMapping("/validate-password")
    public ResponseEntity<PasswordResponseDTO> verifyPassword(
            @RequestBody PasswordRequestDTO passwordRequestDTO){

        String token = communityService.verifyPassword(passwordRequestDTO.getId(),passwordRequestDTO.getPassword());

        return new ResponseEntity<>(new PasswordResponseDTO(token), HttpStatus.OK);
    }

    @PostMapping("/comment/{postID}")
    public ResponseEntity<CommunityDTO> postComment(@PathVariable Long postID, @RequestBody AnswerRequestDTO answerRequestDTO){
        CommunityDTO questionDTO= communityService.createComment(postID, answerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionDTO);
    }

    @DeleteMapping("/comment/{postID}/{commentID}")
    public ResponseEntity<CommunityDTO> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postID,
            @PathVariable Long commentID){

        authService.validateToken(authHeader);

        CommunityDTO questionDTO= communityService.deleteComment(postID, commentID);
        return ResponseEntity.status(HttpStatus.OK).body(questionDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuestion(
            @RequestBody EditRequestDTO communityRequestDTO) {

        communityService.updateCommunityNotice(communityRequestDTO.getId(),communityRequestDTO.getToken(),communityRequestDTO);
        return ResponseEntity.ok("Question updated successfully.");
    }

    // delete
    @DeleteMapping("/invalidate-token")
    public ResponseEntity<Void> invalidateToken(@RequestParam String token){
        verificationService.invalidateToken(token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @RequestBody EditRequestDTO editRequestDTO) {

        communityService.removeQuestion(editRequestDTO.getId(),editRequestDTO.getToken());
        return ResponseEntity.ok("Question deleted successfully.");
    }
}
