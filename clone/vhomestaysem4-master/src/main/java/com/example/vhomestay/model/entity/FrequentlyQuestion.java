package com.example.vhomestay.model.entity;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.enums.FrequentlyQuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FrequentlyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String question;
    @Nationalized
    @Column(columnDefinition = "TEXT")
    private String answer;
    @Enumerated(EnumType.STRING)
    private FrequentlyQuestionType type;
    @Enumerated(EnumType.STRING)
    private BaseStatus status;
}
