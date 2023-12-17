package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
public class HouseholdService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String serviceDescription;
    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    @JsonBackReference
    private Household household;
    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    @JsonBackReference
    private Service service;
    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
}
