package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.RoomPriceUpdateDto;
import com.example.vhomestay.model.dto.response.roomtype.customer.HouseholdRoomTypeForCustomerResponse;
import com.example.vhomestay.model.entity.HouseholdRoomType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdRoomTypeRepository extends BaseRepository<HouseholdRoomType, Long> {

    @Query("select new com.example.vhomestay.model.dto.response.RoomPriceUpdateDto(hrt.id, rt.roomTypeName, hrt.price, hrt.priceUpdate) " +
            "from HouseholdRoomType hrt join hrt.roomType rt where hrt.id = :id")
    Optional<RoomPriceUpdateDto> findHouseholdRoomTypeDetailById(Long id);

    @Query("SELECT hrt FROM HouseholdRoomType hrt JOIN hrt.household h JOIN hrt.roomType rt WHERE h.id = ?1 AND rt.id = ?2")
    Optional<HouseholdRoomType> findByHouseholdIdAndRoomTypeId(Long householdId, Long roomTypeId);

    @Query("SELECT hrt.id FROM HouseholdRoomType hrt join hrt.household h WHERE h.id = ?1 AND hrt.status != 'DELETED' AND hrt.status != 'ACTIVE_PRICE_SET_FIRST_TIME'")
    List<Long> findIdByHouseholdId(Long householdId);
    List<HouseholdRoomType> findAllByHouseholdId(Long householdId);
    @Query("SELECT hrt FROM HouseholdRoomType hrt join hrt.household h join h.manager m join m.account a " +
            "WHERE a.email = :managerEmail AND hrt.status != 'DELETED'")
    List<HouseholdRoomType> getAllHouseholdRoomTypeByManagerEmail(String managerEmail);
    @Query("SELECT hrt FROM HouseholdRoomType hrt join hrt.rooms r join r.homestay hs WHERE hs.homestayCode = :homestayCode AND hrt.status != 'DELETED'")
    List<HouseholdRoomType> getAllHouseholdRoomTypeByHomestayCode(String homestayCode);
    @Query("SELECT hrt FROM HouseholdRoomType hrt join hrt.household h join hrt.roomType rt where h.id = :id and rt.isDorm = true and hrt.status != 'DELETED'")
    List<HouseholdRoomType> findDormitoryByHouseholdId(Long id);

    @Query("SELECT new com.example.vhomestay.model.dto.response.roomtype.customer.HouseholdRoomTypeForCustomerResponse(" +
            "rt.roomTypeName, hrt.capacity, rt.singleBed, rt.doubleBed) " +
            "FROM HouseholdRoomType hrt join hrt.household h join hrt.roomType rt WHERE h.id = :householdId")
    List<HouseholdRoomTypeForCustomerResponse> getRoomTypeListForCustomer(Long householdId);

    @Query("SELECT hrt.price FROM HouseholdRoomType hrt WHERE hrt.id = ?1")
    BigDecimal getPriceByHouseholdRoomTypeId(Long householdRoomTypeId);

    @Query("SELECT rt.isDorm FROM HouseholdRoomType hrt JOIN hrt.roomType rt WHERE hrt.id = ?1")
    boolean getIsDormitoryByHouseholdRoomTypeId(Long householdRoomTypeId);

    @Query("SELECT hrt FROM HouseholdRoomType hrt JOIN hrt.household h  JOIN h.manager m JOIN m.account a WHERE a.email = :s AND hrt.id = :id")
    Optional<HouseholdRoomType> getByManagerEmailAndId(String s, Long id);

    @Query("SELECT hrt.id FROM HouseholdRoomType hrt join hrt.roomType rt join hrt.household h " +
            "WHERE h.id = ?1 AND hrt.status != 'DELETED' AND hrt.status != 'ACTIVE_PRICE_SET_FIRST_TIME' " +
            "AND rt.isDorm = false AND hrt.price > 0")
    List<Long> findIdNotDormByHouseholdId(Long householdId);

    @Query("SELECT hrt.id FROM HouseholdRoomType hrt join hrt.roomType rt join hrt.household h " +
            "WHERE h.id = ?1 AND hrt.status != 'DELETED' AND hrt.status != 'ACTIVE_PRICE_SET_FIRST_TIME' " +
            "AND rt.isDorm = true AND hrt.price > 0")
    List<Long> findIdDormByHouseholdId(Long householdId);
}
