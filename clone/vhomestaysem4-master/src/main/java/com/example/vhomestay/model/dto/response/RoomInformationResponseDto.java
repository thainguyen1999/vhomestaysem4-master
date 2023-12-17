package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.RoomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RoomInformationResponseDto {
    private Long id;
    private String homestayCode;
    private String roomName;
    private String  roomTypeName;
    private Integer capacity;
    private BigDecimal price;
    private BigDecimal priceUpdate;
    private RoomStatus roomStatus;

    public RoomInformationResponseDto(Long id, String homestayCode, String roomName, String roomTypeName, Integer capacity, BigDecimal price, BigDecimal priceUpdate, RoomStatus roomStatus) {
        this.id = id;
        this.homestayCode = homestayCode;
        this.roomName = roomName;
        this.roomTypeName = roomTypeName;
        this.capacity = capacity;
        this.price = price;
        this.priceUpdate = priceUpdate;
        this.roomStatus = roomStatus;
    }
}
