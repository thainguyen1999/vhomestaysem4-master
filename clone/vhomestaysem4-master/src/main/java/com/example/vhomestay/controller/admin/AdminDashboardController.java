package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.response.dashboard.admin.DashboardForAdmin;
import com.example.vhomestay.model.dto.response.dashboard.admin.ListTotalGuestForAdmin;
import com.example.vhomestay.service.impl.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminServiceImpl adminService;

    @GetMapping
    DashboardForAdmin getAdminDashboard(){

        return adminService.getAdminDashboard();
    }
}
