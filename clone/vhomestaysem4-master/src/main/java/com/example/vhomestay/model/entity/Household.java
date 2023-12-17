package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.HouseholdStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Household {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    @Column(nullable = false, unique = true)
    private String householdName;
    private String phoneNumberFirst;
    private String phoneNumberSecond;
    @Column(unique = true)
    private String email;
    private String linkFacebook;
    private String linkTiktok;
    private String linkYoutube;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String avatar;
    private String coverImage;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer cancellationPeriod;
    @Enumerated(EnumType.STRING)
    private HouseholdStatus status;
    private Integer top;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @OneToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    @JsonBackReference
    private Manager manager;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<Homestay> homestay;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<HouseholdService> householdServices;
    @OneToOne(mappedBy = "household")
    @JsonManagedReference
    private HouseholdBankInformation householdBankInformation;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<HouseholdRoomType> householdRoomTypes;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<Booking> bookings;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "household")
    @JsonManagedReference
    private List<Request> requests;
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
