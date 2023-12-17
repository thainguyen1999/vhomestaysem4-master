package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.RoomTypeDto;
import com.example.vhomestay.model.entity.RoomType;
import com.example.vhomestay.service.RoomTypeService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/room-types")
@RequiredArgsConstructor
public class RoomTypeAdminController {
    private final RoomTypeService roomTypeService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllRoomTypesByAdmin() {
        List<RoomTypeDto> roomTypeDtos = roomTypeService.getAllRoomTypesByAdmin();

        Map<String, Object> response = Map.of("roomTypeDtos", roomTypeDtos);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addRoomTypeByAdmin(@RequestBody RoomTypeDto roomTypeDto) {
        roomTypeService.addRoomTypeByAdmin(roomTypeDto);

        return ResponseEntity.ok("household.room.type.add.success");
    }

    @GetMapping("/{id}/form-update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getRoomTypeById(@PathVariable("id") Long id) {
        Optional<RoomTypeDto> roomTypeDto = roomTypeService.getRoomTypeById(id);

        if (roomTypeDto.isEmpty()) {
            throw new ResourceNotFoundException("household.room.type.not.found");
        }
        return ResponseEntity.ok(roomTypeDto);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateRoomTypeByAdmin(@RequestBody RoomTypeDto roomTypeDto) {
        roomTypeService.updateRoomTypeByAdmin(roomTypeDto);

        return ResponseEntity.ok("household.room.type.update.success");
    }
}
