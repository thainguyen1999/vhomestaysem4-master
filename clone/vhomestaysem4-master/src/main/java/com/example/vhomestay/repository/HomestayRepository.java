package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.HomestayDto;
import com.example.vhomestay.model.dto.response.homestay.HomestayDetailForCustomerResponse;
import com.example.vhomestay.model.dto.response.household.admin.HomestayDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.household.customer.HomestayIntroductionDto;
import com.example.vhomestay.model.entity.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long>, BaseRepository<Homestay, Long> {
    Optional<Homestay> findByHomestayCode(String homestayCode);
    @Query("SELECT h FROM Homestay h WHERE h.id = :homestayId AND h.status != 'DELETED'")
    Optional<Homestay> findHomestayById(@Param("homestayId") Long homestayId);
    Optional<Homestay> getHomestayByIdAndHouseholdId(Long homestayId, Long HouseholdId);
    @Query("select new com.example.vhomestay.model.dto.response.HomestayDto(hs.id, hs.homestayCode, hs.fullAddress) from Homestay hs join hs.household h " +
            "where h.id = :householdId and hs.status != 'DELETED'")
    List<HomestayDto> findAllByHouseholdId(Long householdId);
    @Query("select hs from Homestay hs join hs.household h " +
            "where h.id = :householdId and hs.status != 'DELETED'")
    List<Homestay> findAllHomestayByHouseholdId(Long householdId);
    @Query("select hs from Homestay hs join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and hs.status != 'DELETED'")
    List<Homestay> findAllHomestayByManagerEmail(String managerEmail);
    @Query("select hs.fullAddress from Homestay hs where hs.household.id = :id and hs.status = 'ACTIVE'")
    List<String> getAddress(Long id);
    @Query("select new com.example.vhomestay.model.dto.response.household.admin.HomestayDetailForAdminResponse(" +
            "hs.id ,hs.homestayCode, hs.status, hs.rooms) from Homestay hs join hs.household h " +
            "where h.id = :householdId and hs.status != 'DELETED'")
    List<HomestayDetailForAdminResponse> getAllHomestayDetailByHouseholdId(Long householdId);
    @Query("select new com.example.vhomestay.model.dto.response.homestay.HomestayDetailForCustomerResponse(" +
            "hs.id, hs.homestayCode, hs.fullAddress) " +
            "from Homestay hs join hs.household h " +
            "where h.id = :householdId and hs.status != 'DELETED'")
    List<HomestayDetailForCustomerResponse> getHomestayListForCustomer(Long householdId);
    @Query("select count(hs) from Homestay hs join hs.household h join h.manager m join m.account a " +
            "where a.email = :managerEmail and hs.status != 'DELETED'")
    Integer countAllHomestayByManagerEmail(String managerEmail);
    @Query("SELECT h FROM Homestay h WHERE h.status != 'DELETED'")
    List<Homestay> findAllHomestays();
    @Query("SELECT h FROM Homestay h JOIN h.area a WHERE h.status != 'DELETED' AND a.id = :areaId")
    List<Homestay> findHomestaysByAreaId(@Param("areaId") Long areaId);
    @Query("SELECT SUM(hrt.capacity) FROM HouseholdRoomType hrt JOIN hrt.household h JOIN h.homestay hs WHERE hs.id = :homestayId AND hrt.status != 'DELETED'")
    Integer capacityOfHomestay(@Param("homestayId") Long homestayId);
    @Query("SELECT COUNT(ds.id) FROM DormSlot ds JOIN ds.room r JOIN r.homestay hs WHERE hs.id = :homestayId AND ds.status != 'DELETED'")
    Integer countAllDormSlotOfHomestayById(@Param("homestayId") Long homestayId);
    List<Homestay> findByHouseholdId(Long householdId);

    @Query("SELECT h FROM Homestay h JOIN h.household ho JOIN ho.manager m JOIN m.account a WHERE a.email = :managerEmail AND h.id = :homestayId")
    Optional<Homestay> getHomestayByManagerEmailAndHomestayId(String managerEmail, Long homestayId);

    @Query("SELECT h.homestayCode FROM Homestay h JOIN h.household ho WHERE ho.id = :id AND h.status != 'DELETED'")
    List<String> getAllHomestayCodeByHouseholdId(Long id);
    @Query("SELECT h FROM Homestay h WHERE h.status = 'ACTIVE'")
    List<Homestay> findAllActive();
    @Query("SELECT h FROM Homestay h JOIN h.household ho WHERE ho.id = :householdId AND h.status = 'ACTIVE'")
    List<Homestay> findAllActiveByHouseholdId(@Param("householdId") Long householdId);
    @Query("SELECT count(h) FROM Homestay h WHERE h.status != 'DELETED'")
    Integer countAllHomestay();
    @Query("SELECT new com.example.vhomestay.model.dto.response.household.customer.HomestayIntroductionDto(hs.id, a.id, a.name, hs.homestayCode, hh.id, hh.householdName, hh.avatar, hh.description, hh.status) " +
            "FROM Homestay hs JOIN hs.household hh JOIN hs.area a WHERE hs.status != 'DELETED' ORDER BY a.name, hs.homestayCode")
    List<HomestayIntroductionDto> getAllHomestayIntroduction();
}
