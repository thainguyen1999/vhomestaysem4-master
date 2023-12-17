package com.example.vhomestay.model.dto.response.feedback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AddFeedbackForm {
    private String bookingCode;
    private String homestayCode;
    private Integer totalRoom;
    private Integer totalGuest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;

    public AddFeedbackForm(String bookingCode, String homestayCode, Integer totalRoom, Integer totalGuest, LocalDate checkInDate, LocalDate checkOutDate, BigDecimal totalPrice) {
        this.bookingCode = bookingCode;
        this.homestayCode = homestayCode;
        this.totalRoom = totalRoom;
        this.totalGuest = totalGuest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
    }
}
