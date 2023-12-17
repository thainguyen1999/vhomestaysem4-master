package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Data
@Entity
@ToString
public class Facility{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Nationalized
    private String facilityName;
    @OneToMany(mappedBy = "facility")
    @JsonManagedReference
    private List<RoomTypeFacility> roomFacility;
    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
