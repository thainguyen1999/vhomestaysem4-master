package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.admin.HouseholdInfoTopResponseDto;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdDetailForCustomer;
import com.example.vhomestay.model.dto.response.household.customer.HouseholdInTopDto;
import com.example.vhomestay.model.entity.Household;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdRepository extends BaseRepository<Household, Long> {
    @Query("select h from Household h where h.status = 'ACTIVE'")
    List<Household> getAllHousehold();
    @Query("select h from Household h where h.status != 'DELETED'")
    List<Household> getAllHouseholdNotDelete();
    @Query("select h from Household h where h.householdName = :householdName and h.status != 'DELETED'")
    Optional<Household> findByHouseholdName(String householdName);
    @Query("SELECT h FROM Household h join h.manager m join m.account a WHERE a.email = :managerEmail")
    Optional<Household> findByManagerEmail(String managerEmail);
    @Query("select h from Household h where h.status = 'ACTIVE'")
    List<Household> findAllActive();
    @Query("select distinct new com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto(h.id, h.householdName) from Household h join h.homestay hs where hs != null and h.status = 'ACTIVE'")
    List<HouseholdNameDto> findAllHouseholdName();
    @Query("select h from Household h where h.id = :householdId and h.status = 'ACTIVE'")
    Optional<Household> findByIdAndStatusActive(Long householdId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.household.customer.HouseholdDetailForCustomer(" +
            "h.id, h.householdName, h.avatar, h.coverImage, h.phoneNumberFirst, h.phoneNumberSecond, h.email, h.linkFacebook, h.linkTiktok, h.description, h.linkYoutube) " +
            "FROM Household h WHERE h.id = :householdId")
    HouseholdDetailForCustomer getHouseholdDetailForCustomer(Long householdId);

    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto(h.id, h.householdName) " +
            "FROM Household h WHERE h.status = 'ACTIVE' and h.manager is null")
    List<HouseholdNameDto> findAllHouseholdNameHaveNotManager();

    @Query("SELECT h FROM Household h JOIN h.manager m JOIN m.account a WHERE a.id = :accountId AND h.status != 'DELETED'")
    Optional<Household> findByAccountManagerId(Long accountId);
    @Query("SELECT count(h) FROM Household h WHERE h.status != 'DELETED'")
    Integer countAllHousehold();
    @Query("SELECT new com.example.vhomestay.model.dto.response.household.admin.HouseholdInfoTopResponseDto(h.id, h.householdName, h.avatar, '', m.firstName, m.lastName, m.phoneNumber, h.top) " +
            "FROM Household h JOIN h.manager m WHERE h.status != 'DELETED' AND h.top != NULL ORDER BY h.top ASC")
    List<HouseholdInfoTopResponseDto> getTopHousehold();
    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.customer.HouseholdNameDto(h.id, h.householdName) " +
            "FROM Household h WHERE h.status != 'DELETED' AND h.status != 'INACTIVE' AND h.top = NULL")
    List<HouseholdNameDto> getHouseholdNotInTop();
    @Query("SELECT h FROM Household h WHERE h.status != 'DELETED' AND h.top = :top")
    Optional<Household> findByHouseHoldTOP(int top);
    @Query("SELECT new com.example.vhomestay.model.dto.response.household.customer.HouseholdInTopDto(h.id, h.householdName, '', h.avatar, AVG(0), MIN(hrt.price), h.top) " +
            "FROM Household h JOIN h.householdRoomTypes hrt " +
            "WHERE h.status != 'DELETED' AND h.status != 'INACTIVE' AND h.top != NULL " +
            "AND hrt.status != 'DELETED' AND hrt.status != 'ACTIVE_PRICE_SET_FIRST_TIME' " +
            "GROUP BY h.id, h.householdName, h.coverImage, h.top " +
            "ORDER BY h.top ASC")
    List<HouseholdInTopDto> getTopHouseholdForHomePage();

    @Query("SELECT h.top FROM Household h WHERE h.top != NULL ORDER BY h.top ASC")
    List<Integer> getTopList();

    @Query("SELECT count(h) FROM Household h WHERE h.status != 'DELETED'")
    Integer getNumberOfHousehold();
}
