package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class DormSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer slotNumber;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;
    @Enumerated(EnumType.STRING)
    private BaseStatus status;
    @OneToMany(mappedBy = "dormSlot")
    @JsonManagedReference
    private List<BookingDetail> bookingDetails;
}
