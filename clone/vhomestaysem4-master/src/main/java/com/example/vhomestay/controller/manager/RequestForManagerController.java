package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.request.RequestForManagerDto;
import com.example.vhomestay.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/manager/request")
@RequiredArgsConstructor
public class RequestForManagerController {
    private final RequestService requestService;


    @GetMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getRequestByManager(){
        List<RequestForManagerDto> requests = requestService.getRequestByManager();
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteRequestByManager(@PathVariable("requestId") Long requestId) {
        requestService.deleteRequestByManager(requestId);
        return ResponseEntity.ok("request.delete.success");
    }
}
