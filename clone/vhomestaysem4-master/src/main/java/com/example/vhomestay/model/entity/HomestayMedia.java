package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
public class HomestayMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    @Enumerated(EnumType.STRING)
    private MediaType type;
    @ManyToOne
    @JoinColumn(name = "homestay_id", referencedColumnName = "id")
    @JsonBackReference
    private Homestay homestay;
    @ManyToOne
    @JoinColumn(name = "household_room_type_id", referencedColumnName = "id")
    @JsonBackReference
    private HouseholdRoomType householdRoomType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomestayMedia that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
