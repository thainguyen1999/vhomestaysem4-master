package com.example.vhomestay.controller.manager;

import com.example.vhomestay.model.dto.response.dashboard.manager.DashboardForManager;
import com.example.vhomestay.model.dto.response.dashboard.manager.HomestayInformationForDashboard;
import com.example.vhomestay.service.impl.ManagerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/manager/dashboard")
@RequiredArgsConstructor
public class ManagerDashboardController {

    private final ManagerServiceImpl managerService;

    @GetMapping
    public ResponseEntity<?> getManagerDashboard(){
        DashboardForManager dashboardForManager = managerService.getManagerDashboard();
        List<HomestayInformationForDashboard> homestayInformationForDashboardList = managerService.getHomestayInformationForDashboard();
        Map<String, Object> response = Map.of(
                "dashboard", dashboardForManager,
                "roomStatusManagement", homestayInformationForDashboardList
        );
        return ResponseEntity.ok(response);
    }
}
