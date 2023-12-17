package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.RoomStatus;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Nationalized
    private String roomName;
    @Nationalized
    private String description;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @ManyToOne
    @JoinColumn(name = "household_room_type_id", referencedColumnName = "id")
    @JsonBackReference
    private HouseholdRoomType householdRoomType;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    @ManyToOne
    @JoinColumn(name = "homestay_id", referencedColumnName = "id")
    @JsonBackReference
    private Homestay homestay;
    @OneToMany(mappedBy = "room")
    @JsonManagedReference
    private List<DormSlot> dormSlots;
    @OneToMany(mappedBy = "room")
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
