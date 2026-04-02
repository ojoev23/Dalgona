package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.*;
import com.joeolapurath.dalgona.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
                                                         @RequestBody AdminAccountUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAccount(id, request));
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        adminService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getOrderById(id));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        adminService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/umbrellas")
    public ResponseEntity<List<UmbrellaResponse>> getUmbrellas() {
        return ResponseEntity.ok(adminService.getAllUmbrellas());
    }

    @PostMapping("/umbrellas")
    public ResponseEntity<UmbrellaResponse> createUmbrella() {
        return ResponseEntity.ok(adminService.createUmbrella());
    }


    @DeleteMapping("/umbrellas/{id}")
    public ResponseEntity<Void> deleteUmbrella(@PathVariable Long id) {
        adminService.deleteUmbrella(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> getStations() {
        return ResponseEntity.ok(adminService.getAllStations());
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody Map<String, Object> body) {
        String location = (String) body.get("location");
        Number capacity = (Number) body.get("capacity");
        int parsedCapacity = capacity == null ? 0 : capacity.intValue();
        return ResponseEntity.ok(adminService.createStation(location, parsedCapacity));
    }

    @PutMapping("/stations/{id}")
    public ResponseEntity<StationResponse> updateStation(@PathVariable Long id,
                                                         @RequestBody AdminStationUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateStation(id, request));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        adminService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
