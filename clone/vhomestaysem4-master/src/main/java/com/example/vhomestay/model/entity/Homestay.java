package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.HomestayStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Homestay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String homestayCode;
    @Nationalized
    private String description;
    private Double latitude;
    private Double longitude;
    @Nationalized
    private String fullAddress;
    @ManyToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
    @Enumerated(EnumType.STRING)
    private HomestayStatus status;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    @JsonBackReference
    private Household household;
    @OneToMany(mappedBy = "homestay", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JsonManagedReference
    private List<Room> rooms;
    @OneToMany(mappedBy = "homestay")
    @JsonManagedReference
    private List<HomestayMedia> medias;
    @OneToMany(mappedBy = "homestay")
    @JsonManagedReference
    private List<BookingDetail> bookingDetails;
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
