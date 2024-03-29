package com.group.foodorderapplicationback.controller;

import com.group.foodorderapplicationback.model.Admin;
import com.group.foodorderapplicationback.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/get-admin-info")
    public ResponseEntity<Admin> getAdminInfo(HttpServletRequest request) {
        return ResponseEntity.ok().body(adminService.getAdminInfo(request));
    }

    @PutMapping("/admin/update")
    public ResponseEntity<Admin> updateAdmin(HttpServletRequest request, @RequestBody Admin admin) {
        return ResponseEntity.ok().body(adminService.update(request, admin));
    }

}
