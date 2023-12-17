package com.example.vhomestay.model.dto.request.household;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddHouseholdRequest {
    private Long householdId;
    private String householdName;
    private MultipartFile avatar;
}
