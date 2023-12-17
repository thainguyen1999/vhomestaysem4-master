package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.AccountRole;
import com.example.vhomestay.enums.AccountStatus;
import com.example.vhomestay.enums.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    private String otp;
    private LocalDateTime otpCreationTime;
    @OneToOne(mappedBy = "account")
    @JsonManagedReference
    private Admin admin;
    @OneToOne(mappedBy = "account")
    @JsonManagedReference
    private Manager manager;
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Customer customer;
    @CreatedDate
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    @Column(unique = true)
    private String providerId;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
