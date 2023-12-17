package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.RequestStatus;
import com.example.vhomestay.enums.RequestType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
public class Request extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String requestTitle;
    @Nationalized
    private String requestContent;
    @Column
    private String requestData;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    @Nationalized
    private String requestResponse;
    @ManyToOne
    @JoinColumn(name = "household_id", referencedColumnName = "id")
    @JsonBackReference
    private Household household;
    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    @JsonBackReference
    private Admin admin;
    @CreatedDate
    private LocalDateTime createdDate;
    private LocalDateTime solvedDate;
}

