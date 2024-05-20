package com.lld.userms.repositories;

import com.lld.userms.models.Token;
import com.lld.userms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findTokenByValue(String value);

    @Query("SELECT COUNT(t) FROM Token t WHERE t.user = :userId AND t.isActive = true")
    long countActiveTokensByUserId(@Param("userId") User userId);
}
