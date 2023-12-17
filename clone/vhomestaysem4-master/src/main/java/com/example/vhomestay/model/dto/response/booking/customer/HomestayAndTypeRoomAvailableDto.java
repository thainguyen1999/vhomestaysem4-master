package com.example.vhomestay.model.dto.response.booking.customer;

import lombok.Data;

import java.util.List;

@Data
public class HomestayAndTypeRoomAvailableDto {
    private Long homestayId;
    private String homestayCode;
    private List<String> imageUriList;
    private List<RoomTypeHouseholdAvailableWithFullInfoDto> roomTypeAvailableList;
    private Integer capacityAvailable;

}
