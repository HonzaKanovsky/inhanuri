package com.Inhanuri.exchange_notice_board.controller;

import com.Inhanuri.exchange_notice_board.dto.*;
import com.Inhanuri.exchange_notice_board.service.AuthService;
import com.Inhanuri.exchange_notice_board.service.QAService;
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
@RequestMapping("/api/questions")
public class QAController {

    @Autowired
    private QAService qaService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private AuthService authService;


    @GetMapping
    public ResponseEntity<Page<QuestionsTableItemDTO>> getAllQuestions(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(qaService.getQuestions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable Long id, @RequestParam(value = "isFirstVisit", defaultValue = "false") Boolean isFirstVisit){
        return ResponseEntity.status(HttpStatus.OK).body(qaService.getQuestion(id, isFirstVisit));
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<QuestionsTableItemDTO>> getFilteredNotices(
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @PageableDefault Pageable pageable) {

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort));

        Page<QuestionsTableItemDTO> result;

            if ("title".equalsIgnoreCase(filterBy) && query != null) {
            result = qaService.findByTitleContaining(query, sortedPageable);
        } else if ("tag".equalsIgnoreCase(filterBy) && query != null) {
            result = qaService.findByTag(query, sortedPageable);
        } else {
            result = qaService.getQuestions(sortedPageable);
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<QuestionDTO> postQuestion(@RequestBody QuestionRequestDTO questionRequestDTO){
        QuestionDTO questionDTO= qaService.createQuestion(questionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionDTO);
    }

    @PostMapping("/answer/{questionID}")
    public ResponseEntity<QuestionDTO> postAnswer(@PathVariable Long questionID, @RequestBody AnswerRequestDTO answerRequestDTO){
        QuestionDTO questionDTO= qaService.createAnswer(questionID, answerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionDTO);
    }

    @DeleteMapping("/answer/{postID}/{answerID}")
    public ResponseEntity<QuestionDTO> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postID,
            @PathVariable Long answerID){

        authService.validateToken(authHeader);

        QuestionDTO questionDTO= qaService.deleteAnswer(postID, answerID);
        return ResponseEntity.status(HttpStatus.OK).body(questionDTO);
    }

    @PostMapping("/validate-password")
    public ResponseEntity<PasswordResponseDTO> verifyPassword(
            @RequestBody PasswordRequestDTO passwordRequestDTO) {

        String token = qaService.verifyPassword(passwordRequestDTO.getId(),passwordRequestDTO.getPassword());

        return new ResponseEntity<>(new PasswordResponseDTO(token), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuestion(
            @RequestBody EditRequestDTO editRequestDTO) {

        qaService.updateQuestion(editRequestDTO);
        return ResponseEntity.ok("Question updated successfully.");
    }

    @DeleteMapping("/invalidate-token")
    public ResponseEntity<Void> invalidateToken(@RequestParam String token){
        verificationService.invalidateToken(token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @RequestBody EditRequestDTO editRequestDTO) {

        qaService.removeQuestion(editRequestDTO.getId(),editRequestDTO.getToken());
        return ResponseEntity.ok("Question deleted successfully.");
    }
}
