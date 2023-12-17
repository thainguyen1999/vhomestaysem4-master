package com.example.vhomestay.repository;

import com.example.vhomestay.model.dto.response.booking.customer.FeedbackHouseholdDto;
import com.example.vhomestay.model.dto.response.dashboard.manager.LowFeedbackDetailForManager;
import com.example.vhomestay.model.dto.response.feedback.EditFeedbackForm;
import com.example.vhomestay.model.dto.response.feedback.FeedbackForManagerResponse;
import com.example.vhomestay.model.entity.Feedback;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends BaseRepository<Feedback, Long>{
    @Query("SELECT fb FROM Feedback fb join fb.customer c " +
            "join c.account a WHERE a.email = :email")
    List<Feedback> getFeedbacksByEmail(@Param("email") String email);
    List<Feedback> getFeedbacksByCustomerId(Long id);
    @Query("SELECT new com.example.vhomestay.model.dto.response.feedback.EditFeedbackForm(" +
            "b.bookingCode, hs.homestayCode, b.totalRoom, b.totalGuest,b.checkInDate, b.checkOutDate, " +
            "b.totalPrice, fb.content, fb.rating) " +
            "FROM Feedback fb join fb.booking b join b.bookingDetails bdt join bdt.homestay hs WHERE fb.id = :feedbackId")
    Optional<EditFeedbackForm> getBookingDetailByBookingCodeForEditFeedback(Long feedbackId);
    @Query("SELECT distinct new com.example.vhomestay.model.dto.response.feedback.FeedbackForManagerResponse(" +
            "fb.id, b.bookingCode, b.totalRoom, b.totalGuest,b.checkInDate, b.checkOutDate, b.createdDate, " +
            "b.totalPrice, fb.content, fb.rating, fb.status, c.firstName, c.lastName, ca.email) " +
            "FROM Feedback fb join fb.customer c join c.account ca join fb.booking b " +
            "join fb.household h join h.manager m join m.account a WHERE a.email = :managerEmail and fb.status != 'DELETED'")
    List<FeedbackForManagerResponse> getAllFeedbackByManagerEmail(@Nullable String managerEmail);
    @Query("SELECT new com.example.vhomestay.model.dto.response.feedback.FeedbackForManagerResponse(" +
            "fb.id, b.bookingCode,b.totalRoom, b.totalGuest,b.checkInDate, b.checkOutDate, b.createdDate, " +
            "b.totalPrice, fb.content, fb.rating, fb.status, c.firstName, c.lastName, ca.email) " +
            "FROM Feedback fb join fb.customer c join c.account ca join fb.booking b " +
            "join fb.household h join h.manager m join m.account a WHERE a.email = :managerEmail " +
            "and fb.status != 'DELETED' order by fb.rating asc")
    List<FeedbackForManagerResponse> getAllFeedbackByManagerEmailSortByRating(@Nullable String managerEmail, Pageable pageable);
    @Query("SELECT AVG(fb.rating) FROM Feedback fb join fb.household h where h.id = :id and fb.status = 'SHOWED'")
    BigDecimal getRatingAverageByHouseholdId(Long id);
    @Query("SELECT COUNT(fb) FROM Feedback fb join fb.household h where h.id = :id and fb.status = 'SHOWED'")
    Integer countFeedbackByHousehold(Long id);
    @Query("SELECT new com.example.vhomestay.model.dto.response.booking.customer.FeedbackHouseholdDto(" +
            "fb.id, c.firstName, c.lastName, c.avatar, fb.rating, fb.content) " +
            "FROM Feedback fb join fb.customer c join fb.household h where h.id = :id and fb.status = 'SHOWED'")
    List<FeedbackHouseholdDto> getFeedbackByHouseholdId(Long id);
    @Query("select fb from Feedback fb join fb.booking b join b.household h join h.manager m join m.account a" +
            " where a.email = :managerEmail and fb.status != 'DELETED'")
    List<Feedback> getFeedbackByManagerEmail(String managerEmail);
    @Query("select new com.example.vhomestay.model.dto.response.dashboard.manager.LowFeedbackDetailForManager(" +
            "fb.id, b.bookingCode, fb.createdDate, c.firstName, c.lastName, fb.rating) from Feedback fb join fb.customer c join fb.booking b " +
            "join b.household h join h.manager m join m.account a" +
            " where a.email = :managerEmail and fb.status != 'DELETED' and fb.rating < 3")
    List<LowFeedbackDetailForManager> getLowFeedbackDetailForManager(String managerEmail);

    @Query("SELECT AVG(fb.rating) FROM Feedback fb JOIN fb.household h WHERE h.id = :id AND fb.status = 'SHOWED' GROUP BY h.id")
    Double getRatingByHouseholdId(Long id);
}
