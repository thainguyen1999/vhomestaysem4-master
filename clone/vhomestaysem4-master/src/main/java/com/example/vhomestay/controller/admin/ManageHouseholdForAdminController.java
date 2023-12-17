package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.request.household.AddHouseholdRequest;
import com.example.vhomestay.model.dto.request.household.HouseholdTOPRequestDto;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailListForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdInfoTopResponseDto;
import com.example.vhomestay.model.entity.Household;
import com.example.vhomestay.service.HouseholdService;
import com.example.vhomestay.service.impl.HouseholdServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/household")
@RequiredArgsConstructor
public class ManageHouseholdForAdminController {
    private final HouseholdService householdService;

    @GetMapping
    public HouseholdDetailListForAdminResponse getAllHousehold() {
        List<Household> householdList = householdService.getAllHousehold();
        List<HouseholdDetailForAdminResponse> householdDetailForAdminResponseList = new ArrayList<>();
        int size = householdList.size();
        int i;
        for (i = 0; i< size; i++){
            HouseholdDetailForAdminResponse householdDetailForAdminResponse = householdService.getHouseholdDetailForAdmin(householdList.get(i).getId());
            householdDetailForAdminResponseList.add(householdDetailForAdminResponse);
        }
        return HouseholdDetailListForAdminResponse.builder().householdDetailListForAdmin(householdDetailForAdminResponseList).build();
    }

    @PostMapping("/add-household")
    public ResponseEntity<?> addHousehold(@ModelAttribute AddHouseholdRequest addHouseholdRequest) throws IOException {
        if (householdService.isHouseholdNameExist(addHouseholdRequest.getHouseholdName())){
            throw new ResourceBadRequestException("household.name.exist");
        }
        if (householdService.uploadHouseholdAvatar(addHouseholdRequest.getAvatar(), addHouseholdRequest.getHouseholdName())){
            return ResponseEntity.ok("household.add.success");
        } else {
            throw new ResourceBadRequestException("household.update.success");
        }
    }

    @PutMapping("/edit-household")
    public ResponseEntity<?> editHousehold(@ModelAttribute AddHouseholdRequest addHouseholdRequest) throws IOException {
        if (householdService.updateHouseholdAvatar(addHouseholdRequest.getAvatar(), addHouseholdRequest.getHouseholdName(), addHouseholdRequest.getHouseholdId())){
            return ResponseEntity.ok("household.update.success");
        } else {
            throw new ResourceBadRequestException("household.update.failed");
        }
    }

    @DeleteMapping("/delete-household/{householdId}")
    public ResponseEntity<?> deleteHousehold(@PathVariable("householdId") Long householdId) throws IOException {
        if (householdService.deleteHousehold(householdId)){
            return ResponseEntity.ok("household.delete.success");
        } else {
            throw new ResourceBadRequestException("household.delete.failed");
        }
    }

    @PutMapping("/hide-household/{householdId}")
    public ResponseEntity<?> hideHousehold(@PathVariable("householdId") Long householdId) throws IOException {
        if (householdService.HideOrShowHousehold(householdId)){
            return ResponseEntity.ok("household.hide.success");
        } else {
            throw new ResourceBadRequestException("household.hide.failed");
        }
    }

    @PutMapping("/show-household/{householdId}")
    public ResponseEntity<?> showHousehold(@PathVariable("householdId") Long householdId) throws IOException {
        if (householdService.HideOrShowHousehold(householdId)){
            return ResponseEntity.ok("household.show.success");
        } else {
            throw new ResourceBadRequestException("household.show.failed");
        }
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopHousehold(){
        List<HouseholdInfoTopResponseDto> householdList = householdService.getTopHousehold();
        List<HouseholdNameDto> householdNotInTop = householdService.getHouseholdNotInTop();
        List<Integer> topList = householdService.getTopList();
        Map<String, Object> response = Map.of(
                "householdList", householdList,
                "topList", topList,
                "householdNotInTop", householdNotInTop
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/top")
    public ResponseEntity<?> setTopHousehold(@RequestBody HouseholdTOPRequestDto householdTOPRequestDto){
        householdService.setTopHousehold(householdTOPRequestDto);
        return ResponseEntity.ok("household.set.position.success");
    }

    @DeleteMapping("/top/{top}")
    public ResponseEntity<?> deleteTopHousehold(@PathVariable("top") String top){
        try {
            int topNumber = Integer.parseInt(top);
            if (topNumber < 1 || topNumber > 5){
                throw new ResourceBadRequestException("household.top.invalid");
            }
            householdService.deleteTopHousehold(topNumber);
        } catch (NumberFormatException e){
            throw new ResourceBadRequestException("household.top.is.not.number");
        }
        return ResponseEntity.ok("household.delete.position.success");
    }



}
