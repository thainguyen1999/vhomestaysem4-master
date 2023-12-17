package com.example.vhomestay.controller.manager;

import com.example.vhomestay.service.CancellationHistoryService;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/manager/cancellation-history")
@RequiredArgsConstructor
public class CancellationHistoryManagerController {
    private final CancellationHistoryService cancellationHistoryService;

    @PutMapping("/{bookingCode}/refund")
    public ResponseEntity<?> refundBookingByManager(@PathVariable String bookingCode){
        cancellationHistoryService.refundBookingByManager(bookingCode);

        return ResponseEntity.ok("payment.refund.success");
    }
}
