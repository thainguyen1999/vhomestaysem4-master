package com.example.vhomestay.model.dto.response.bookingdetail;

import com.example.vhomestay.enums.BookingDetailStatus;
import com.example.vhomestay.model.dto.response.DormSlotFormDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Data
public class BookingDetailCheckInOutTodayResponseDto {
    private Long homestayId;
    private String homestayCode;
    private Long roomTypeId;
    private String roomTypeName;
    private Long roomId;
    private String roomName;
    private Long totalSlot;
    private Boolean isDorm;

    public BookingDetailCheckInOutTodayResponseDto(Long homestayId, String homestayCode, Long roomTypeId, String roomTypeName, Long roomId, String roomName, Long totalSlot, Boolean isDorm) {
        this.homestayId = homestayId;
        this.homestayCode = homestayCode;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalSlot = totalSlot;
        this.isDorm = isDorm;
    }

    public BookingDetailCheckInOutTodayResponseDto(String homestayCode, Long roomTypeId, String roomTypeName, Long roomId, String roomName, Long totalSlot, Boolean isDorm) {
        this.homestayCode = homestayCode;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalSlot = totalSlot;
        this.isDorm = isDorm;
    }
}
