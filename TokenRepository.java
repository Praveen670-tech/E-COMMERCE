package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByEmail(String email);
}
