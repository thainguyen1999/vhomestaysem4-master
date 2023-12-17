package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.request.RoomCreateRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingRandomDormSlotManagerRequestDto;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.dto.response.room.RoomSearchManagerResponseDto;
import com.example.vhomestay.model.entity.Household;
import com.example.vhomestay.model.entity.Room;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.HomestayService;
import com.example.vhomestay.service.HouseholdRoomTypeService;
import com.example.vhomestay.service.RoomService;
import com.example.vhomestay.service.impl.HouseholdServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/manager/room")
@RequiredArgsConstructor
public class RoomController {
    private final HomestayService homestayService;
    private final HouseholdRoomTypeService householdRoomTypeService;
    private final RoomService roomService;
    private final HouseholdServiceImpl householdService;

    @GetMapping
    public RoomInformationListResponseDto getAllRoomDetailForManager() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        String managerEmail = currentUserLoginEmailOptional.get();
        List<RoomInformationResponseDto> roomDetailResponseDtoList = roomService.getRoomListByManagerEmail(managerEmail);

        return RoomInformationListResponseDto.builder()
                .roomListForManager(roomDetailResponseDtoList)
                .build();
    }

    @GetMapping("/dorm")
    public DormListForManager getAllDormDetailForManager() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        if (!SecurityUtil.isManager()){
            throw new ResourceBadRequestException("no.permission");
        }
        String managerEmail = currentUserLoginEmailOptional.get();

        List<DormInformationResponseDto> dormDetailResponseDtoList = roomService.getDormListByManagerEmail(managerEmail);

        return DormListForManager.builder()
                .dormListForManager(dormDetailResponseDtoList)
                .build();
    }

    @GetMapping("/form-data")
    public RoomCreateFormResponseDto addRoom() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        Household household = householdService.getHouseholdByManagerEmail(currentUserLoginEmailOptional.get()).get();
        Long householdId = household.getId();
        List<HomestayDto> homestayDtoList = homestayService.findHomestayByHouseholdId(householdId);
        if (homestayDtoList.isEmpty()) {
            throw new ResourceBadRequestException("household.do.not.have.homestay");
        }
        List<HouseholdRoomTypeDto> householdRoomTypeDtoList = householdRoomTypeService.findHouseholdRoomTypeByHouseholdId(householdId);
        if (householdRoomTypeDtoList.isEmpty()) {
            throw new ResourceBadRequestException("household.room.type.do.not.have.household.room.type.or.no.permission");
        }
        return RoomCreateFormResponseDto.builder()
                .homestay(homestayDtoList)
                .roomType(householdRoomTypeDtoList)
                .build();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> addRoom(@RequestBody RoomCreateRequestDto roomCreateFormResponseDto) {
        if (roomService.addRoom(roomCreateFormResponseDto)) {
            return ResponseEntity.ok("room.add.success");
        } else {
            throw new ResourceBadRequestException("room.add.failed");
        }
    }

    @GetMapping("/dorm/form-data")
    public RoomCreateFormResponseDto addDorm() {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("User not found");
        }
        Household household = householdService.getHouseholdByManagerEmail(currentUserLoginEmailOptional.get()).get();
        Long householdId = household.getId();
        List<HomestayDto> homestayDtoList = homestayService.findHomestayByHouseholdId(householdId);
        if (homestayDtoList.isEmpty()) {
            throw new ResourceBadRequestException("Household don't have any homestay yet");
        }
        List<HouseholdRoomTypeDto> householdRoomTypeDtoList = householdRoomTypeService.findHouseholdDormByHouseholdId(householdId);
        if (householdRoomTypeDtoList.isEmpty()) {
            throw new ResourceBadRequestException("Household don't have any type room yet or type room are not accepted by admin");
        }
        return RoomCreateFormResponseDto.builder()
                .homestay(homestayDtoList)
                .roomType(householdRoomTypeDtoList)
                .build();
    }

    @PostMapping("/dorm")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> addDormSlot(@RequestBody DormSlotFormDto dormSlotFormDto) {
        if (roomService.addDormSlot(dormSlotFormDto)) {
            return ResponseEntity.ok("dorm.add.success");
        } else {
            throw new ResourceBadRequestException("dorm.add.failed");
        }
    }

    @DeleteMapping("/dorm")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> removeDormSlot(@RequestBody DormSlotFormDto dormSlotFormDto) {
        if (roomService.removeDormSlot(dormSlotFormDto)) {
            return ResponseEntity.ok("dorm.delete.success");
        } else {
            throw new ResourceBadRequestException("dorm.delete.failed");
        }
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public RoomInformationDto getRoomDetail(@PathVariable("roomId") Long roomId) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        Room room = roomService.findById(roomId).get();
        RoomInformationDto roomInformationDto = roomService.mapToDTO(room);
        Integer totalDomSlot = roomService.countAllDormSlotByRoomId(roomId);
        roomInformationDto.setTotalDormSlot(totalDomSlot);
        return roomInformationDto;
    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> editRoom(@PathVariable Long roomId, @RequestBody RoomEditDto roomEditDto) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()){
            throw new ResourceBadRequestException("account.not.found");
        }
        if (roomService.editRoom(roomId, roomEditDto)) {
            return ResponseEntity.ok("room.update.success");
        } else {
            throw new ResourceBadRequestException("room.update.failed");
        }
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()) {
            throw new ResourceBadRequestException("account.not.found");
        }
        if (roomService.deleteRoom(roomId)) {
            return ResponseEntity.ok("room.delete.success");
        } else {
            throw new ResourceBadRequestException("room.delete.failed");
        }
    }

    @DeleteMapping("/{roomId}/hide")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> hideRoom(@PathVariable Long roomId) {
        Optional<String> currentUserLoginEmailOptional = SecurityUtil.getCurrentUserLogin();
        if (currentUserLoginEmailOptional.isEmpty()) {
            throw new ResourceBadRequestException("account.not.found");
        }
        if (roomService.hideOrShowRoom(roomId)) {
            return ResponseEntity.ok("room.hide.success");
        } else {
            throw new ResourceBadRequestException("room.hide.failed");
        }
    }

    @GetMapping("/search-to-book")
    public ResponseEntity<?> searchAvailableRoomsByManager(@RequestParam(value = "homestayId", required = false) String homestayId,
                                                           @RequestParam("checkInDate") String checkInDate,
                                                           @RequestParam("checkOutDate") String checkOutDate) {

        List<RoomSearchManagerResponseDto> roomSearchManagerResponseDtoList = roomService.searchAvailableRoomsWithTotalDormSlotByManager(homestayId, checkInDate, checkOutDate);

        Map<String, Object> response = new HashMap<>();
        response.put("rooms", roomSearchManagerResponseDtoList);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/random-dorm-slots")
    public ResponseEntity<?> randomDormSlotAvailableByManager(@RequestBody BookingRandomDormSlotManagerRequestDto bookingRandomDormSlotManagerRequestDto){
        // Kiểm tra xem số lượng phòng và dorm slot còn lại có đủ không
        boolean isEnough = roomService.checkRoomAndTotalSlotAvailability(bookingRandomDormSlotManagerRequestDto);

        // Nếu đủ thì random ra các dorm slot
        if (isEnough){
            BookingCreateManagerRequestDto roomDormSlotResponseDto = roomService.randomDormSlotAvailableByManager(bookingRandomDormSlotManagerRequestDto);

            return ResponseEntity.ok(roomDormSlotResponseDto);
        }
        else {
            throw new ResourceBadRequestException("room.or.dorm.slot.not.enough");
        }
    }
}
