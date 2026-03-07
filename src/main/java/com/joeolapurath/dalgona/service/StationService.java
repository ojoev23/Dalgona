package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.StationResponse;
import com.joeolapurath.dalgona.model.Station;
import com.joeolapurath.dalgona.model.StationSlot;
import com.joeolapurath.dalgona.repository.StationRepository;
import com.joeolapurath.dalgona.repository.StationSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationSlotRepository stationSlotRepository;

    public StationService(StationRepository stationRepository, StationSlotRepository stationSlotRepository) {
        this.stationRepository = stationRepository;
        this.stationSlotRepository = stationSlotRepository;
    }

    public List<StationResponse> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StationResponse getStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return toResponse(station);
    }

    @Transactional
    public StationResponse createStation(String location, int capacity) {
        Station station = Station.builder()
                .location(location)
                .capacity(capacity)
                .build();
        station = stationRepository.save(station);

        // Create empty slots for the station
        for (int i = 1; i <= capacity; i++) {
            StationSlot slot = StationSlot.builder()
                    .station(station)
                    .slotIndex(i)
                    .umbrella(null)
                    .build();
            stationSlotRepository.save(slot);
        }

        // Reload to get slots populated
        station = stationRepository.findById(station.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return toResponse(station);
    }

    public Station getStationEntity(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
    }

    private StationResponse toResponse(Station station) {
        int occupied = (int) stationSlotRepository.countByStationAndUmbrellaIsNotNull(station);
        int available = station.getCapacity() - occupied;
        return StationResponse.builder()
                .stationId(station.getStationId())
                .location(station.getLocation())
                .capacity(station.getCapacity())
                .occupied(occupied)
                .available(available)
                .build();
    }
}
