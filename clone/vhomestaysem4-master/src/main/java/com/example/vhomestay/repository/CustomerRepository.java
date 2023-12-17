package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.user.UserInfoResponseDto;
import com.example.vhomestay.model.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c join c.account a WHERE c.id = :id AND a.status = 'ACTIVE'")
    Optional<Customer> getCustomerByIdAndAccount_Status_Active(@Param("id") Long id);
    Optional<Customer> getCustomerByAccountEmail(String email);
    @Query("SELECT c FROM Customer c join c.account a WHERE a.email = :managerEmail")
    Optional<Customer> findByEmail(String managerEmail);
    @Query("SELECT new com.example.vhomestay.model.dto.response.user.UserInfoResponseDto(a.id, c.avatar, a.email, c.firstName, c.lastName, a.role, a.createdDate, '',  a.status) " +
            "FROM Customer c JOIN c.account a WHERE a.status != 'DELETED'")
    List<UserInfoResponseDto> findAllByAdmin();
    @Query("SELECT count(c) FROM Customer c join c.account a WHERE a.status = 'ACTIVE'")
    Integer countAllCustomer();
}
