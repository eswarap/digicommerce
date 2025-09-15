package org.woven.digicommerce.tokensvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.woven.digicommerce.tokensvc.entity.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndRevokedFalse(String token);
}