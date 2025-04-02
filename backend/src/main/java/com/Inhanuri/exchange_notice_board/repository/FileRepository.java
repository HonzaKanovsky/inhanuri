package com.Inhanuri.exchange_notice_board.repository;


import com.Inhanuri.exchange_notice_board.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileData, Long> {
}
