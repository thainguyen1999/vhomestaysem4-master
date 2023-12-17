package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Data
@Entity
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String image;
    @Column(nullable = false)
    @Nationalized
    private String serviceName;
    @Column(nullable = false)
    @Nationalized
    private String description;
    @OneToMany(mappedBy = "service")
    @JsonManagedReference
    private List<HouseholdService> householdServices;
    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
}
