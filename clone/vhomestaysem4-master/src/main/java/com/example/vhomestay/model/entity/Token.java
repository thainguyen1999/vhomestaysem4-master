package com.example.vhomestay.model.entity;

import com.example.vhomestay.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, unique = true)
    public String token;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked; //stop token active

    public boolean expired; //extend token life

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Account user;

    public Account getUserEntity() {
        return user;
    }
}
