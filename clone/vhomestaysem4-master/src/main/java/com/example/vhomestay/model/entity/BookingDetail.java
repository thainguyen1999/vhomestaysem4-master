package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.BookingDetailStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String checkInCustomerName;
    private BigDecimal price;
    private BigDecimal subTotal;
    @Enumerated(EnumType.STRING)
    private BookingDetailStatus status;
    @ManyToOne
    @JoinColumn(name = "booking_code", referencedColumnName = "bookingCode")
    @JsonBackReference
    private Booking booking;
    @ManyToOne
    @JoinColumn(name = "household_room_type_id", referencedColumnName = "id")
    @JsonBackReference
    private HouseholdRoomType householdRoomType;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @JsonBackReference
    private Room room;
    @ManyToOne
    @JoinColumn(name = "homestay_id", referencedColumnName = "id")
    @JsonBackReference
    private Homestay homestay;
    @ManyToOne
    @JoinColumn(name = "dorm_slot_id", referencedColumnName = "id")
    @JsonBackReference
    private DormSlot dormSlot;
}
