package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String content;
    private Integer rating;
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonBackReference
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    @JsonBackReference
    private Household household;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_code", referencedColumnName = "bookingCode")
    @JsonBackReference
    private Booking booking;
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
