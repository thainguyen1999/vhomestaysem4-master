package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.household.HouseholdInfoRequestDto;
import com.example.vhomestay.model.dto.request.household.HouseholdMediaRequestDto;
import com.example.vhomestay.model.dto.request.household.HouseholdTOPRequestDto;
import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdInfoTopResponseDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdResponseDto;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdDetailForCustomer;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdInTopDto;
import com.example.vhomestay.model.dto.response.user.HouseholdInfoResponseDto;
import com.example.vhomestay.model.entity.Household;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface HouseholdService extends BaseService<Household, Long>{
    List<Household> getAllHousehold();
    Optional<Household> getHouseholdByManagerEmail();
    HouseholdDetailForCustomer getHouseholdDetailForCustomer(Long householdId);
    Optional<Household> getHouseholdByManagerEmail(String email);
    HouseholdResponseDto mapToDTO(Household household);
    Household mapToEntity(HouseholdResponseDto householdResponseDto);
    boolean updateHouseholdInformation(HouseholdInfoRequestDto householdInfoRequestDto);
    boolean updateHouseholdMedia(HouseholdMediaRequestDto householdMediaRequestDto);
    List<HouseholdNameDto> findAllHouseholdName();
    HouseholdDetailForAdminResponse getHouseholdDetailForAdmin(Long householdId);
    boolean uploadHouseholdAvatar(MultipartFile image, String householdName) throws IOException;
    boolean updateHouseholdAvatar(MultipartFile image, String householdName , Long householdId) throws IOException;
    String deleteHouseholdAvatar(Long householdId);
    boolean isHouseholdNameExist(String householdName);
    boolean HideOrShowHousehold(Long householdId);
    boolean deleteHousehold(Long householdId);
    HouseholdInfoResponseDto getHouseholdInfoByAdmin(Long accountId);
    public List<HouseholdInfoTopResponseDto> getTopHousehold();
    List<HouseholdNameDto> getHouseholdNotInTop();
    void setTopHousehold(HouseholdTOPRequestDto householdTOPRequestDto);
    void deleteTopHousehold(int top);
    List<HouseholdInTopDto> findAllHouseholdInTop();
    List<Integer> getTopList();
}
