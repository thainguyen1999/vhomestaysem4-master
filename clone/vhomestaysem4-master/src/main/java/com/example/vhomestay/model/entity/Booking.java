package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@ToString
public class Booking {
    @Id
    private String bookingCode;
    private Integer totalRoom;
    private Integer totalGuest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private String checkInName;
    private String checkInPhoneNumber;
    private Integer totalNight;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
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
    @OneToOne(mappedBy = "booking")
    @JsonManagedReference
    private Feedback feedback;
    @OneToMany(mappedBy = "booking", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<BookingDetail> bookingDetails;
    @OneToOne(mappedBy = "booking", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private Payment payment;
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonManagedReference
    private CancellationHistory cancellationHistory;
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
