package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.StationResponse;
import com.joeolapurath.dalgona.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> getStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> getStation(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody Map<String, Object> body) {
        String location = (String) body.get("location");
        int capacity = (int) body.get("capacity");
        return ResponseEntity.ok(stationService.createStation(location, capacity));
    }
}
