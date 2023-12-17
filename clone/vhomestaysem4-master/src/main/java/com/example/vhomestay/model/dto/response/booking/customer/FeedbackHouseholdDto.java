package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FeedbackHouseholdDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private Integer rating;
    private String content;

    public FeedbackHouseholdDto(Long id, String firstName, String lastName, String avatar, Integer rating, String content) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.rating = rating;
        this.content = content;
    }
}
