package com.joeolapurath.dalgona.repository;

import com.joeolapurath.dalgona.model.Station;
import com.joeolapurath.dalgona.model.StationSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationSlotRepository extends JpaRepository<StationSlot, Long> {

    List<StationSlot> findByStationOrderBySlotIndexAsc(Station station);

    Optional<StationSlot> findByStationAndSlotIndex(Station station, int slotIndex);

    Optional<StationSlot> findFirstByStationAndUmbrellaIsNullOrderBySlotIndexAsc(Station station);

    long countByStationAndUmbrellaIsNotNull(Station station);

    long countByStationAndUmbrellaIsNull(Station station);
}

