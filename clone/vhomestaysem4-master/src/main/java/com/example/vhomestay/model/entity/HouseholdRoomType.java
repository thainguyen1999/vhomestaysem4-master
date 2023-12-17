package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.HouseholdTypeRoomStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@ToString
public class HouseholdRoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal price;
    private BigDecimal priceUpdate;
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    private HouseholdTypeRoomStatus status;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isChildrenAndBed;
    @ManyToOne
    @JoinColumn(name = "room_type_id", referencedColumnName = "id")
    @JsonBackReference
    private RoomType roomType;
    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    @JsonBackReference
    private Household household;
    @OneToMany(mappedBy = "householdRoomType")
    @JsonManagedReference
    private List<Room> rooms;
    @OneToMany(mappedBy = "householdRoomType")
    @JsonManagedReference
    private List<RoomTypeFacility> roomTypeFacilities;
    @OneToMany(mappedBy = "householdRoomType")
    @JsonManagedReference
    private List<BookingDetail> bookingDetails;
    @OneToMany(mappedBy = "householdRoomType")
    @JsonManagedReference
    private List<HomestayMedia> homestayMedias;

}
