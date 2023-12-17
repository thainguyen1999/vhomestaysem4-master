package com.example.vhomestay.model.dto.response.household.admin;

import com.example.vhomestay.enums.HomestayStatus;
import com.example.vhomestay.model.entity.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HomestayDetailForAdminResponse {
    private Long homestayId;
    private String homestayCode;
    private HomestayStatus homestayStatus;
    private Integer numberOfRoom;
    private Integer numberOfDorm;
    private Integer totalCapacity;

    public HomestayDetailForAdminResponse(Long homestayId, String homestayCode, HomestayStatus homestayStatus, List<Room> roomList) {
        this.homestayId = homestayId;
        this.homestayCode = homestayCode;
        this.homestayStatus = homestayStatus;
    }
}
