package com.example.nms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "device")
public class Device {
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name="device")
    private String device; 
    
    @Column(name="deviceName	")
    private String deviceName;
	public Device() {
		super();
	}
	public Device(Long id, String device, String deviceName) {
		super();
		this.id = id;
		this.device = device;
		this.deviceName = deviceName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	@Override
	public String toString() {
		return "Device [id=" + id + ", device=" + device + ", deviceName=" + deviceName + "]";
	}

    
}

