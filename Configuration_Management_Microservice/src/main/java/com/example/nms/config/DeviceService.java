package com.example.nms.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.nms.repository.DeviceRepository;

import org.springframework.stereotype.Service;

import com.example.nms.model.Device;

import java.util.List;

@Service
public class DeviceService {

	
	private final DeviceRepository deviceRepository;

	@Autowired
	public DeviceService(DeviceRepository deviceRepository) {
		super();
		this.deviceRepository = deviceRepository;
	}


	public List<Device> getAllDevices() {

		return deviceRepository.findAll();
	}
}
