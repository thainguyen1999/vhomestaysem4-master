package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.NotificationType;
import com.example.vhomestay.model.dto.request.NotificationBookingCancelRequest;
import com.example.vhomestay.model.dto.request.NotificationBookingSuccessRequest;
import com.example.vhomestay.model.dto.request.NotificationRequestPriceRequest;
import com.example.vhomestay.model.entity.Notification;
import com.example.vhomestay.repository.ManagerRepository;
import com.example.vhomestay.repository.NotificationRepository;
import com.example.vhomestay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.minidev.json.JSONObject;
import org.aspectj.weaver.ast.Not;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ManagerRepository managerRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void pushBookingSuccessNotification(Long toWhom, NotificationBookingSuccessRequest notificationRequest) {
        Notification notification = new Notification();

        Map<String, String> data = Map.of(
                "type", "booking_success",
                "title", notificationRequest.getTitle(),
                "customerName", notificationRequest.getCustomerName(),
                "totalGuest", notificationRequest.getTotalGuest(),
                "checkInDate", notificationRequest.getCheckInDate(),
                "checkOutDate", notificationRequest.getCheckOutDate(),
                "bookingCode", notificationRequest.getBookingCode()
        );

        JSONObject jsonObject = new JSONObject(data);

        notification.setContent(jsonObject.toString());
        notification.setIsRead(false);
        notification.setType(NotificationType.BOOKING_SUCCESS);
        notification.setToWhom(managerRepository.findById(toWhom).get());

        notification = notificationRepository.save(notification);

        data = Map.of(
                "id", notification.getId().toString(),
                "type", notification.getType().toString(),
                "title", notificationRequest.getTitle(),
                "customerName", notificationRequest.getCustomerName(),
                "totalGuest", notificationRequest.getTotalGuest(),
                "checkInDate", notificationRequest.getCheckInDate(),
                "checkOutDate", notificationRequest.getCheckOutDate(),
                "bookingCode", notificationRequest.getBookingCode(),
                "isRead", notification.getIsRead().toString()
        );

        messagingTemplate.convertAndSendToUser(toWhom.toString(), "/notifications", notification);
    }

    @Override
    public void pushBookingCancelNotification(Long toWhom, NotificationBookingCancelRequest bookingCancelRequest) {
        Notification notification = new Notification();

        Map<String, String> data = Map.of(
                "type", "booking_cancel",
                "title", bookingCancelRequest.getTitle(),
                "customerName", bookingCancelRequest.getCustomerName(),
                "bookingCode", bookingCancelRequest.getBookingCode(),
                "refundAmount", bookingCancelRequest.getRefundAmount().toString(),
                "deadlineRefundDate", bookingCancelRequest.getDeadlineRefundDate().toString()
        );

        JSONObject jsonObject = new JSONObject(data);

        notification.setContent(jsonObject.toString());
        notification.setIsRead(false);
        notification.setType(NotificationType.BOOKING_CANCEL);
        notification.setToWhom(managerRepository.findById(toWhom).get());

        notification = notificationRepository.save(notification);

        data = Map.of(
                "id", notification.getId().toString(),
                "type", notification.getType().toString(),
                "title", bookingCancelRequest.getTitle(),
                "customerName", bookingCancelRequest.getCustomerName(),
                "bookingCode", bookingCancelRequest.getBookingCode(),
                "refundAmount", bookingCancelRequest.getRefundAmount().toString(),
                "deadlineRefundDate", bookingCancelRequest.getDeadlineRefundDate().toString(),
                "isRead", notification.getIsRead().toString()
        );

        messagingTemplate.convertAndSendToUser(toWhom.toString(), "/notifications", notification);
    }

    @Override
    public void pushRequestResultNotification(Long toWhom, NotificationRequestPriceRequest notificationRequest) {
        Notification notification = new Notification();

        Map<String, String> data = Map.of(
                "type", "request_result",
                "title", notificationRequest.getTitle(),
                "householdRoomTypeName", notificationRequest.getHouseholdRoomTypeName(),
                "requestStatus", notificationRequest.getRequestStatus().toString()
        );

        JSONObject jsonObject = new JSONObject(data);

        notification.setContent(jsonObject.toString());
        notification.setIsRead(false);
        notification.setType(NotificationType.REQUEST_RESULT);
        notification.setToWhom(managerRepository.findById(toWhom).get());

        notification = notificationRepository.save(notification);

        data = Map.of(
                "id", notification.getId().toString(),
                "type", notification.getType().toString(),
                "title", notificationRequest.getTitle(),
                "householdRoomTypeName", notificationRequest.getHouseholdRoomTypeName(),
                "requestStatus", notificationRequest.getRequestStatus().toString(),
                "isRead", notification.getIsRead().toString()
        );

        messagingTemplate.convertAndSendToUser(toWhom.toString(), "/notifications", notification);
    }

    @Override
    public void sendOnlineNotification(Long userId) {
        List<Notification> notificationList = notificationRepository.findNotificationByToWhomIdAndIsRead(userId, false);


        messagingTemplate.convertAndSendToUser(userId.toString(), "/get-unread-notifications", notificationList);
    }

    @Override
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

}
