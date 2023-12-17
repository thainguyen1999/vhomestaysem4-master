package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Nationalized;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isRead;
    @ManyToOne
    @JoinColumn(name = "to_whom", referencedColumnName = "id")
    @JsonBackReference
    private Manager toWhom;
}
