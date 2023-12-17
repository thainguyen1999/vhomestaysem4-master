package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.model.entity.ContactUs;
import com.example.vhomestay.model.entity.VillageInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactUsResponseDto {
    private VillageInformation villageInformation;
    private List<ContactUs> contactUs;
}
