package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.VillageInformationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VillageInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Long totalVisitedCustomer;
    private Long totalVisitor;
    @Enumerated(EnumType.STRING)
    private VillageInformationType type;
    @OneToMany(mappedBy = "villageInformation")
    @JsonManagedReference
    private List<VillageMedia> images;
}
