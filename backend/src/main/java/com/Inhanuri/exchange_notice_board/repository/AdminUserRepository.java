package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, String> {
    AdminUser findAdminUserByUsername(String username);
}
