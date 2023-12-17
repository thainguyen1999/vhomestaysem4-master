package com.example.vhomestay.model.dto.response.booking.manager;

import com.example.vhomestay.enums.RefundStatus;
import com.example.vhomestay.model.entity.CancellationReason;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class BookingCancelManagerResponseDto {
    private String bookingCode;
    private BigDecimal refundAmount;
    private List<CancellationReason> cancellationReasons;
    private List<RefundStatus> refundStatuses;
    private Integer cancellationPeriod;
}
