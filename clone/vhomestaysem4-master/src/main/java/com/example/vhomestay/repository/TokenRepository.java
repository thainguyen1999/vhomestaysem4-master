package com.example.vhomestay.repository;

import com.example.vhomestay.model.entity.Account;
import com.example.vhomestay.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query(value = "SELECT t FROM Token t JOIN t.user a " +
            "WHERE a.email = :email and (t.expired = false or t.revoked = false) and a.status = 'ACTIVE'")
    Optional<Token> findUserByEmail(String email);
    List<Token> findAllByUser(Account account);
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserId(Long accountId);
    @Query("SELECT t FROM Token t join t.user a WHERE a.email = :userEmail")
    Optional<Token> deleteAllByUserEmail(String userEmail);
}
