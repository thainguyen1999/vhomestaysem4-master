package com.example.vhomestay.model.dto.response.feedback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class EditFeedbackForm {
    private String bookingCode;
    private String homestayCode;
    private Integer totalRoom;
    private Integer totalGuest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    @Nationalized
    private String content;
    private int rating;

    public EditFeedbackForm(String bookingCode, String homestayCode, Integer totalRoom, Integer totalGuest, LocalDate checkInDate, LocalDate checkOutDate, BigDecimal totalPrice, String content, int rating) {
        this.bookingCode = bookingCode;
        this.homestayCode = homestayCode;
        this.totalRoom = totalRoom;
        this.totalGuest = totalGuest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.content = content;
        this.rating = rating;
    }
}
