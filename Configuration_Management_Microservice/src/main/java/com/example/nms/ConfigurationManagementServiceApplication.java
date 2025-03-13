package com.example.nms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.nms")
public class ConfigurationManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigurationManagementServiceApplication.class, args);
	}

}
