package com.usktea.plainold.repositories;

import com.usktea.plainold.models.token.Token;
import com.usktea.plainold.models.user.Username;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<Token, Username> {
    Optional<Token> findByNumber(String number);
}
