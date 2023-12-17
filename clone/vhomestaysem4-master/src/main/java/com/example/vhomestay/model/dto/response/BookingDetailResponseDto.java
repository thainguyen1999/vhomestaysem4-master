package com.example.vhomestay.model.dto.response;

import com.example.vhomestay.enums.BookingDetailStatus;
import com.example.vhomestay.model.entity.Homestay;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookingDetailResponseDto {
    private Long id;
    private String homestayCode;
    private String homestayAddress;
    private String homestayPhoneNumber;
    private String checkInCustomerName;
    private Integer capacityOfRoomType;
    private String roomTypeName;
    private BigDecimal price;
    private List<String> facilities;
    private Boolean isChildrenAndBed;
    private BookingDetailStatus status;
}
