package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.dto.response.manager.ManagerDetailResponse;
import com.example.vhomestay.model.entity.Manager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends BaseRepository<Manager, Long> {
    @Query("SELECT m " +
            "FROM Manager m JOIN m.account a WHERE a.email = :email AND a.status = 'ACTIVE'")
    Optional<Manager> findByAccountEmail(String email);

    @Query("SELECT new com.example.vhomestay.model.dto.response.user.UserInfoResponseDto(a.id, m.avatar, a.email, m.firstName, m.lastName, a.role, a.createdDate, h.householdName, a.status) " +
            "FROM Manager m JOIN m.account a JOIN m.household h WHERE a.status != 'DELETED'")
    List<UserInfoResponseDto> findAllByAdmin();

    Optional<Manager> findById(Long id);

    @Query("select new com.example.vhomestay.model.dto.response.manager.ManagerDetailResponse(m.id, m.firstName, m.lastName) from Manager m")
    List<ManagerDetailResponse> getAllManager();
}
