package com.example.vhomestay.controller;

import com.example.vhomestay.model.dto.request.NotificationBookingCancelRequest;
import com.example.vhomestay.model.dto.request.NotificationBookingSuccessRequest;
import com.example.vhomestay.model.dto.request.NotificationRequestPriceRequest;
import com.example.vhomestay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/private/booking-success/{userId}")
    public ResponseEntity<String> sendBookingSuccessNotification(@PathVariable Long userId, @RequestBody NotificationBookingSuccessRequest request) {
        // Xử lý yêu cầu từ client (có thể làm bất kỳ thao tác nào cần thiết ở đây)

        // Sau khi xử lý, gửi thông báo đến client
        notificationService.pushBookingSuccessNotification(userId, request);

        // Trả về kết quả thành công
        return ResponseEntity.ok("noti.send.success");
    }

    @PostMapping("/private/booking-cancel/{userId}")
    public ResponseEntity<String> sendBookingCancelNotification(@PathVariable Long userId, @RequestBody NotificationBookingCancelRequest request) {
        // Xử lý yêu cầu từ client (có thể làm bất kỳ thao tác nào cần thiết ở đây)

        // Sau khi xử lý, gửi thông báo đến client
        notificationService.pushBookingCancelNotification(userId, request);

        // Trả về kết quả thành công
        return ResponseEntity.ok("noti.send.success");
    }

    @PostMapping("/private/request-result/{userId}")
    public ResponseEntity<String> sendRequestResultNotification(@PathVariable Long userId, @RequestBody NotificationRequestPriceRequest request) {
        // Xử lý yêu cầu từ client (có thể làm bất kỳ thao tác nào cần thiết ở đây)

        // Sau khi xử lý, gửi thông báo đến client
        notificationService.pushRequestResultNotification(userId, request);

        // Trả về kết quả thành công
        return ResponseEntity.ok("noti.send.success");
    }

    @MessageMapping("/get-unread-notifications/{userId}")
    public ResponseEntity<String> sendOnlineNotification(@DestinationVariable Long userId) {
        // Xử lý yêu cầu từ client (có thể làm bất kỳ thao tác nào cần thiết ở đây)
        notificationService.sendOnlineNotification(userId);
        // Trả về kết quả thành công
        return ResponseEntity.ok("noti.send.success");
    }

    @PutMapping("/read-notification/{notificationId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> readNotification(@PathVariable Long notificationId) {
        // Xử lý yêu cầu từ client (có thể làm bất kỳ thao tác nào cần thiết ở đây)
        notificationService.readNotification(notificationId);
        // Trả về kết quả thành công
        return ResponseEntity.ok("noti.send.success");
    }

}
