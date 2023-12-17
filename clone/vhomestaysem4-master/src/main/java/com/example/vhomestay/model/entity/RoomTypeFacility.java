package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
public class RoomTypeFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonBackReference
    private Facility facility;
    @ManyToOne
    @JoinColumn(name = "household_room_type_id", referencedColumnName = "id")
    @JsonBackReference
    private HouseholdRoomType householdRoomType;
}
