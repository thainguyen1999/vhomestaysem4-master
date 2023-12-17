package com.example.vhomestay.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.enums.MediaVillagePosition;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
public class VillageMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    @Enumerated(EnumType.STRING)
    private MediaType type;
    @Enumerated(EnumType.STRING)
    private MediaVillagePosition position;
    @ManyToOne
    @JoinColumn(name = "local_product_id", referencedColumnName = "id")
    @JsonBackReference
    private LocalProduct localProduct;
    @ManyToOne
    @JoinColumn(name = "village_information_id", referencedColumnName = "id")
    @JsonBackReference
    private VillageInformation villageInformation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VillageMedia that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
