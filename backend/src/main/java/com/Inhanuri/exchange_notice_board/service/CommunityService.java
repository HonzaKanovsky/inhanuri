package com.Inhanuri.exchange_notice_board.service;

import com.Inhanuri.exchange_notice_board.dto.*;
import com.Inhanuri.exchange_notice_board.enums.RegexPatterns;
import com.Inhanuri.exchange_notice_board.enums.TagType;
import com.Inhanuri.exchange_notice_board.exception.IncompleteRequestException;
import com.Inhanuri.exchange_notice_board.exception.InvalidPasswordException;
import com.Inhanuri.exchange_notice_board.exception.TokenValidationException;
import com.Inhanuri.exchange_notice_board.model.*;
import com.Inhanuri.exchange_notice_board.repository.CommentRepository;
import com.Inhanuri.exchange_notice_board.repository.CommunityNoticeRepository;
import com.Inhanuri.exchange_notice_board.repository.LikeRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommunityService {

    @Autowired
    private CommunityNoticeRepository communityNoticeRepository;


    @Autowired
    private VerificationService verificationService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRecordRepository likeRecordRepository;

    public Page<CommunityTableDTO> getCommunityPage(Pageable pageable) {
        return communityNoticeRepository.findAll(pageable).map(CommunityNotice::toTableDto);
    }

    public CommunityDTO getCommunityNoticeByID(Long id, Boolean isFirstVisit) {
        CommunityNotice communityNotice = doesCommunityNoticeExist(id);

        if(isFirstVisit){
            communityNotice.setViews(communityNotice.getViews() + 1);
            return communityNoticeRepository.save(communityNotice).toDto();
        }

        return communityNotice.toDto();
    }

    public CommunityDTO saveCommunityNotice(CommunityRequestDTO communityRequestDTO) {
        doesCommunityRequestDTOHaveRequiredFields(communityRequestDTO);
        validateQAPassword(communityRequestDTO.getPassword());

        CommunityNotice communityNotice = new CommunityNotice();
        communityNotice.setContent(communityRequestDTO.getContent());
        communityNotice.setTitle(communityRequestDTO.getTitle());
        communityNotice.setTag(communityRequestDTO.getTag());
        communityNotice.setPassword(communityRequestDTO.getPassword());

        return communityNoticeRepository.save(communityNotice).toDto();
    }


    public String verifyPassword(Long id, String password) {

        CommunityNotice communityNotice = doesCommunityNoticeExist(id);

        if (!communityNotice.checkPassword(password))
            throw new InvalidPasswordException("Password is not valid");

        return verificationService.generateToken(id);
    }

    public void updateCommunityNotice(Long id, String token, CommunityRequestDTO communityRequestDTO) {
        if (!verificationService.isTokenValid(token, id))
            throw new TokenValidationException("Unauthorized: Invalid or expired token for this question.");

        editCommunityNoticeData(id, communityRequestDTO);

        verificationService.invalidateToken(token);
    }

    private void editCommunityNoticeData(Long id, CommunityRequestDTO communityRequestDTO) {
        CommunityNotice communityNotice = doesCommunityNoticeExist(id);
        communityNotice.setTitle(communityRequestDTO.getTitle());
        communityNotice.setContent(communityRequestDTO.getContent());
        communityNotice.setTag(communityRequestDTO.getTag());
        communityNotice.setUpdatedAt(LocalDateTime.now());
        communityNoticeRepository.save(communityNotice);
    }

    private void doesCommunityRequestDTOHaveRequiredFields(CommunityRequestDTO communityRequestDTO){
        if (!StringUtils.hasLength(communityRequestDTO.getTitle())
                || !StringUtils.hasLength(communityRequestDTO.getContent())
                || !StringUtils.hasLength(String.valueOf(communityRequestDTO.getTag()))
                || !StringUtils.hasLength(communityRequestDTO.getPassword()))
            throw new IncompleteRequestException("Required fields are missing");
    }

    private void validateQAPassword(String password){
        if(!RegexPatterns.PIN.matches(password))
            throw new InvalidPasswordException("Password is not valid");
    }

    private CommunityNotice doesCommunityNoticeExist(Long id){
        Optional<CommunityNotice> questionOpt = communityNoticeRepository.findById(id);
        if (questionOpt.isEmpty())
            throw new NoSuchElementException("Community notice with this id was not found");
        return questionOpt.get();
    }

    @Transactional
    public void removeQuestion(Long id, String token) {
        if (!verificationService.isTokenValid(token, id))
            throw new TokenValidationException("Unauthorized: Invalid or expired token for this question.");

        deleteCommunityNotice(id);

        verificationService.invalidateToken(token);
    }

    private void deleteCommunityNotice(Long id){
        CommunityNotice communityNotice = doesCommunityNoticeExist(id);

        likeRecordRepository.deleteByCommunityNoticeComments(communityNotice);

        likeRecordRepository.deleteByCommunityNotice(communityNotice);

        communityNoticeRepository.delete(communityNotice);

    }

    public Page<CommunityTableDTO> findByTitleContaining(String title, Pageable pageable) {
        return communityNoticeRepository.findByTitleContainingIgnoreCase(title, pageable).map(CommunityNotice::toTableDto);
    }

    public Page<CommunityTableDTO> findByTag(String tag, Pageable pageable) {

        return communityNoticeRepository.findByTag(TagType.valueOf(tag), pageable).map(CommunityNotice::toTableDto);
    }

    public CommunityDTO createComment(Long postID, AnswerRequestDTO answerRequestDTO) {
        CommunityNotice communityNotice = doesCommunityNoticeExist(postID);

        Comment comment = new Comment();
        comment.setContent(answerRequestDTO.getContent());
        comment.setPost(communityNotice);
        comment.setIsAdmin(answerRequestDTO.getIsAdmin());

        commentRepository.save(comment);

        communityNotice.getComments().add(comment);

        return communityNoticeRepository.save(communityNotice).toDto();
    }

    @Transactional
    public void adminDeleteCommunity(Long id) {
        deleteCommunityNotice(id);
    }

    public void adminEditCommunity(EditRequestDTO editRequestDTO) {
        editCommunityNoticeData(editRequestDTO.getId(), editRequestDTO);

    }

    @Transactional
    public CommunityDTO deleteComment(Long postID, Long commentID) {

        likeRecordRepository.deleteByComment_Id(commentID);
        commentRepository.deleteById(commentID);

        CommunityNotice communityNotice = doesCommunityNoticeExist(postID);
        communityNotice.getComments().removeIf(c -> c.getId().equals(commentID));

        return communityNotice.toDto();
    }
}
