package com.example.vhomestay.controller.manager;

import com.example.vhomestay.service.PaymentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/manager/payments")
@RequiredArgsConstructor
public class PaymentManagerController {
    private final PaymentService paymentService;

    @PutMapping("/{bookingCode}")
    public ResponseEntity<?> editPaymentByManager(@PathVariable String bookingCode) {
        paymentService.editPaymentByManager(bookingCode);

        Map<String, String> response = Map.of("message", "Edit payment success");

        return ResponseEntity.ok(response);
    }
}
