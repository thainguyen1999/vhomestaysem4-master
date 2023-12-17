package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.service.customer.*;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailForAddResponse;
import com.example.vhomestay.model.entity.HouseholdService;
import com.example.vhomestay.model.entity.Service;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends BaseRepository<Service, Long> {
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.manager.ServiceDetailForAddResponse(s.id, s.serviceName) FROM Service s")
    List<ServiceDetailForAddResponse> getAllServiceForAdd();
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.customer.ServiceResponse(s.id, s.serviceName, s.image) FROM Service s where s.status != 'DELETED'")
    List<ServiceResponse> getAllService();
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.customer.ServiceResponse(s.id, s.serviceName, s.image)\n" +
            "FROM Service s\n" +
            "WHERE s.id NOT IN (\n" +
            "    SELECT srv.id\n" +
            "    FROM Service srv\n" +
            "    JOIN srv.householdServices hsv\n" +
            "    JOIN hsv.household h\n" +
            "    JOIN h.manager m\n" +
            "    JOIN m.account a\n" +
            "    WHERE a.email = :managerEmail\n" +
            "    AND hsv.status != 'DELETED'\n" +
            ")")
    List<ServiceResponse> getAllServiceAvailable(String managerEmail);
    @Query("SELECT sv FROM Service sv where sv.serviceName = :serviceName AND sv.status != 'DELETED'")
    Optional<Service> findByServiceName(String serviceName);
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse(" +
            "s.id, hsv.id, s.serviceName, s.image, hsv.serviceDescription, hsv.status) FROM Service s " +
            "join s.householdServices hsv join hsv.household h join h.manager m join m.account a " +
            "WHERE a.email = :managerEmail and hsv.status != 'DELETED'")
    List<com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse> getServicesByManagerEmail(String managerEmail);
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.admin.ServiceDetailResponse(" +
            "s.id, s.serviceName, s.image, s.description ,s.status) FROM Service s where s.status != 'DELETED'")
    List<com.example.vhomestay.model.dto.response.service.admin.ServiceDetailResponse> getAllServices();
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse(" +
            "s.id, hsv.id, s.serviceName, s.image, hsv.serviceDescription, hsv.status) FROM Service s " +
            "join s.householdServices hsv join hsv.household h " +
            "WHERE h.id = :householdId and hsv.status != 'DELETED'")
    List<com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse> getServicesByHouseholdId(Long householdId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.customer.HouseholdByServiceResponse(" +
            "h.id, h.householdName, h.avatar, h.phoneNumberFirst, h.phoneNumberSecond) FROM Service s " +
            "join s.householdServices hsv join hsv.household h " +
            "WHERE s.id IN :serviceIdList " +
            "GROUP BY h.id " +
            "HAVING COUNT(h.id) = :size")
    List<HouseholdByServiceResponse> getHouseholdByServiceId(@Param("serviceIdList") List<Long> serviceIdList, @Param("size") int size);
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.customer.ServiceDetailResponse(s.id, s.serviceName, s.image) FROM Service s WHERE s.id = :serviceId")
    ServiceDetailResponse getServiceById(Long serviceId);
    Optional<Service> findById(Long id);
    @Query("SELECT hsv FROM HouseholdService hsv " +
            "join hsv.service sv where sv.id = :serviceId and hsv.status != 'DELETED'")
    List<HouseholdService> findAllByServiceId(Long serviceId);
    @Query("SELECT hsv FROM HouseholdService hsv " +
            "join hsv.service sv where sv.id = :serviceId and hsv.status = 'ACTIVE'")
    List<HouseholdService> findAllByServiceIdAndStatusIsActive(Long serviceId);
    @Query("SELECT hsv FROM HouseholdService hsv " +
            "join hsv.service sv where sv.id = :serviceId and hsv.status != 'INACTIVE'")
    List<HouseholdService> findAllByServiceIdAndStatusIsDeavtive(Long serviceId);
    @Query("SELECT count(sv) FROM Service sv WHERE sv.status != 'DELETED'")
    Integer countAllService();
    @Query("SELECT new com.example.vhomestay.model.dto.response.service.customer.ServiceIntroductionDto(" +
            "s.serviceName, s.description, s.image) FROM Service s WHERE s.status != 'DELETED' AND s.status != 'INACTIVE'")
    List<ServiceIntroductionDto> getAllServiceInHomePage();
}
