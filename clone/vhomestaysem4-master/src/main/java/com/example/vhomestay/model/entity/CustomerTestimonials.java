package com.example.vhomestay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CustomerTestimonials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String avatar;
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private Integer rating;
    private String job;
    private String address;
}
