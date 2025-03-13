package com.example.nms.ui;

import com.example.nms.config.ConfigBackup;
import com.example.nms.config.ConfigChangeTracker;
import com.example.nms.config.ConfigPush;
import com.example.nms.config.DeviceService;
import com.example.nms.model.Device;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfigController {

	private final ConfigBackup configBackup;
	private final ConfigPush configPush;
	private final ConfigChangeTracker configChangeTracker;
	private final DeviceService deviceService;

	@Autowired
	public ConfigController(ConfigBackup configBackup, ConfigPush configPush, ConfigChangeTracker configChangeTracker, DeviceService deviceService) {
		this.configBackup = configBackup;
		this.configPush = configPush;
		this.configChangeTracker = configChangeTracker;
		this.deviceService = deviceService;
	}

	@GetMapping("/config")
	public String configPage(Model model) {
		
		return "dashboard"; // Return the dashboard page with the device table
	}
	
	@GetMapping("/devices")
    public String viewDevices(Model model) {
        List<Device> devices = deviceService.getAllDevices();
        model.addAttribute("devices", devices);
        return "devices";
    }


	@PostMapping("/push")
	public String pushConfig(@RequestParam("commands") String[] commands) {
		// Pass the commands to the pushConfig method in ConfigPush
		Boolean r = configPush.pushConfig(commands);
		return "redirect:/config"; // Redirect to the config page or dashboard after pushing
	}

	@PostMapping("/backup")
	public String backupConfig(@RequestParam("filename") String filename) {
		configBackup.backupConfig(filename); // Call the method to backup the config
		return "redirect:/config"; // Redirect to the config page after backup
	}
	@PostMapping("/logging")
	public String saveLogs(@RequestParam("timestampFilter") String timestampFilter) {
		configChangeTracker.fetchLogs(timestampFilter); // Call the method to backup the config
        return "redirect:/config"; // Redirect to the config page after backup
	}

	@GetMapping("/submitcommands")
	public String showSubmitCommandsForm() {
		return "submitcommands"; // This corresponds to submitcommands.html in the templates folder
	}

	@GetMapping("/givefilename")
	public String showBackupConfigForm() {
		return "givefilename"; // This corresponds to submitcommands.html in the templates folder
	}
	
	@GetMapping("/givetime")
	public String showBackupLogsForm() {
		return "givetime"; // This corresponds to submitcommands.html in the templates folder
	}


}
