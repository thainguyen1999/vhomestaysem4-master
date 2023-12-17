package com.example.vhomestay.service;

import com.example.vhomestay.model.dto.request.NotificationBookingCancelRequest;
import com.example.vhomestay.model.dto.request.NotificationBookingSuccessRequest;
import com.example.vhomestay.model.dto.request.NotificationRequestPriceRequest;

public interface NotificationService {
    void pushBookingSuccessNotification(Long toWhom, NotificationBookingSuccessRequest notificationRequest);

    void pushBookingCancelNotification(Long toWhom, NotificationBookingCancelRequest bookingCancelRequest);

    void pushRequestResultNotification(Long toWhom, NotificationRequestPriceRequest notificationRequest);
    void sendOnlineNotification(Long userId);
    void readNotification(Long notificationId);
}
