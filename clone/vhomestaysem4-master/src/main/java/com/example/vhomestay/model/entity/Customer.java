package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String firstName;
    @Nationalized
    @Column(nullable = false)
    private String lastName;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    @Nationalized
    private String address;
    private String avatar;
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonBackReference
    private Account account;
    @OneToOne(mappedBy = "customer")
    @JsonManagedReference
    private CustomerBankInformation customerBankInformation;
    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<Booking> bookings;
    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<CancellationHistory> cancellationHistories;
}
