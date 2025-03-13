package com.example.nms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nms.model.Device;


@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}

