package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.vhomestay.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@ToString
public class RoomType extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String roomTypeName;
    private Integer singleBed;
    private Integer doubleBed;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isDorm;
    @OneToMany(mappedBy = "roomType")
    @JsonManagedReference
    private List<HouseholdRoomType> householdRoomTypes;
}
