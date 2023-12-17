package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.RoomCreateRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingDetailCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingRandomDormSlotManagerRequestDto;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.dto.response.room.RoomSearchManagerResponseDto;
import com.example.vhomestay.model.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Optional<Room> findRoomById(Long roomId);
    boolean addRoom(RoomCreateRequestDto roomCreateRequestDto);
    boolean addDormSlot(DormSlotFormDto dormSlotFormDto);
    boolean removeDormSlot(DormSlotFormDto dormSlotFormDto);
    List<RoomInformationResponseDto> getRoomListByManagerEmail(String managerEmail);
    List<DormInformationResponseDto> getDormListByManagerEmail(String managerEmail);
    RoomInformationDto mapToDTO(Room room);
    Room mapToEntity(RoomInformationDto roomInformationDto);
    Optional<Room> findById(Long roomId);
    boolean editRoom(Long roomId, RoomEditDto roomEditDto);
    @Transactional
    boolean deleteRoom(Long roomId);
    Integer countAllDormSlotByRoomId(Long roomId);
    boolean hideOrShowRoom(Long roomId);
    List<RoomSearchManagerResponseDto> searchAvailableRoomsWithTotalDormSlotByManager(String homestayId, String checkInDate, String checkOutDate);
    boolean checkRoomAndTotalSlotAvailability(BookingRandomDormSlotManagerRequestDto bookingRandomDormSlotManagerRequestDto);
    BookingCreateManagerRequestDto randomDormSlotAvailableByManager(BookingRandomDormSlotManagerRequestDto bookingRandomDormSlotManagerRequestDto);
    boolean checkRoomAndDormSlotAvailability(BookingCreateManagerRequestDto bookingCreateManagerRequestDto);
}
