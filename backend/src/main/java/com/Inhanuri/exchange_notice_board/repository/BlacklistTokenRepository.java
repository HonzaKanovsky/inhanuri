package com.Inhanuri.exchange_notice_board.repository;

import com.Inhanuri.exchange_notice_board.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepository extends JpaRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);
}
