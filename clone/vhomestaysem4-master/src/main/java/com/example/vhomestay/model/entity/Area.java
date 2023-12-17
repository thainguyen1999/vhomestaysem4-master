package com.example.vhomestay.model.entity;

import com.example.vhomestay.enums.BaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@ToString
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Area extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String name;
    private String image;
    @Enumerated(EnumType.STRING)
    private BaseStatus status;
    @OneToMany(mappedBy = "area")
    private List<Homestay> homestays;
}
