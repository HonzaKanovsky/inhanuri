package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.*;
import com.Inhanuri.exchange_notice_board.enums.RegexPatterns;
import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.exception.IncompleteRequestException;
import com.Inhanuri.exchange_notice_board.exception.InvalidPasswordException;
import com.Inhanuri.exchange_notice_board.exception.TokenValidationException;
import com.Inhanuri.exchange_notice_board.model.QAAnswer;
import com.Inhanuri.exchange_notice_board.model.QAQuestion;
import com.Inhanuri.exchange_notice_board.repository.AnswerRepository;
import com.Inhanuri.exchange_notice_board.repository.LikeRecordRepository;
import com.Inhanuri.exchange_notice_board.repository.QARepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class QAService {
    @Autowired
    QARepository qaRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    VerificationService verificationService;

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    public String verifyPassword(Long id, String password) {

        QAQuestion question = doesQuestionExist(id);

        if (!question.checkPassword(password))
            throw new InvalidPasswordException("Password is not valid");

        return verificationService.generateToken(id);
    }

    public void updateQuestion(EditRequestDTO editRequestDTO) {
        if (!verificationService.isTokenValid(editRequestDTO.getToken(), editRequestDTO.getId()))
            throw new TokenValidationException("Unauthorized: Invalid or expired token for this question.");

        saveQuestionData(editRequestDTO);

        verificationService.invalidateToken(editRequestDTO.getToken());
    }

    public void adminEditQuestion(EditRequestDTO editRequestDTO) {
        saveQuestionData(editRequestDTO);
    }

    private void saveQuestionData(EditRequestDTO editRequestDTO){
        QAQuestion question = doesQuestionExist(editRequestDTO.getId());
        question.setTitle(editRequestDTO.getTitle());
        question.setContent(editRequestDTO.getContent());
        qaRepository.save(question);
    }

    private QAQuestion doesQuestionExist(Long id){
        Optional<QAQuestion> questionOpt = qaRepository.findById(id);
        if (questionOpt.isEmpty())
            throw new NoSuchElementException("Question with this id was not found");
        return questionOpt.get();
    }

    public QuestionDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        doesQuestionRequestDTOHaveRequiredFields(questionRequestDTO);
        validateQAPassword(questionRequestDTO.getPassword());

        QAQuestion question = new QAQuestion();
        question.setTitle(questionRequestDTO.getTitle());
        question.setContent(questionRequestDTO.getContent());
        question.setPassword(questionRequestDTO.getPassword());
        question.setTag(questionRequestDTO.getTag());
        return qaRepository.save(question).toDto();
    }

    public Page<QuestionsTableItemDTO> getQuestions(Pageable pageable) {
        return qaRepository.findAll(pageable).map(QAQuestion::toTableDto);
    }

    @Transactional
    public void removeQuestion(Long id, String token) {
        if (!verificationService.isTokenValid(token, id))
            throw new TokenValidationException("Unauthorized: Invalid or expired token for this question.");

        QAQuestion question = doesQuestionExist(id);

        deleteQuestionData(question);

        verificationService.invalidateToken(token);
    }

    @Transactional
    public void adminDeleteQuestion(Long id) {
        QAQuestion question = doesQuestionExist(id);

        deleteQuestionData(question);
    }

    private void deleteQuestionData(QAQuestion question) {
        // Step 1: Delete likes associated with comments
        likeRecordRepository.deleteByQuestionAnswers(question);

        // Step 2: Delete likes associated with the community notice itself
        likeRecordRepository.deleteByQuestion(question);


        qaRepository.delete(question);

    }

    public QuestionDTO getQuestion(Long id, Boolean isFirstVisit) {
        QAQuestion question = doesQuestionExist(id);

        if(isFirstVisit){
            question.setViews(question.getViews() + 1);

            return qaRepository.save(question).toDto();
        }
        return question.toDto();
    }

    public QuestionDTO createAnswer(Long questionID, AnswerRequestDTO answerRequestDTO) {
        QAQuestion question = doesQuestionExist(questionID);

        QAAnswer answer = new QAAnswer();
        answer.setContent(answerRequestDTO.getContent());
        answer.setQuestion(question);
        answer.setIsAdmin(answerRequestDTO.getIsAdmin());

        answerRepository.save(answer);

        question.getAnswers().add(answer);

        return qaRepository.save(question).toDto();
    }

    private void doesQuestionRequestDTOHaveRequiredFields(QuestionRequestDTO questionRequestDTO){
        if (!StringUtils.hasLength(questionRequestDTO.getTitle())
                || !StringUtils.hasLength(questionRequestDTO.getContent())
                || !StringUtils.hasLength(String.valueOf(questionRequestDTO.getTag()))
                || !StringUtils.hasLength(questionRequestDTO.getPassword()))
            throw new IncompleteRequestException("Required fields are missing");
    }
    private void validateQAPassword(String password){
        if(!RegexPatterns.PIN.matches(password))
            throw new InvalidPasswordException("Password is not valid");
    }

    public Page<QuestionsTableItemDTO> findByTitleContaining(String title, Pageable pageable) {
        return qaRepository.findByTitleContainingIgnoreCase(title, pageable).map(QAQuestion::toTableDto);
    }

    public Page<QuestionsTableItemDTO> findByTag(String tag, Pageable pageable) {

        return qaRepository.findByTag(TagType.valueOf(tag), pageable).map(QAQuestion::toTableDto);
    }

    @Transactional
    public QuestionDTO deleteAnswer(Long postID, Long answerID) {
        likeRecordRepository.deleteByAnswer_Id(answerID);
        answerRepository.deleteById(answerID);

        QAQuestion question = doesQuestionExist(postID);
        question.getAnswers().removeIf(c -> c.getId().equals(answerID));

        return question.toDto();
    }
}
