package com.example.nms.config;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Component
public class ConfigBackup {
    private String routerIp; // Router IP address
    private int routerPort; // Router port (e.g., 23 for Telnet)
    private String username; // Username for the router
    private String password; // Password for the router
    private String loginCommand; // Command to send the username
    private String enableCommand; // Command to enter enable mode
    private String terminalCommand; // Command to set terminal length
    private String configureCommand; // Command to enter configuration mode
    private String backupPath; // Path to save the backup configuration
    private JSch jsch;
    // Constructor to initialize the properties from config file 
    public ConfigBackup() {
        loadConfig();
    }
    public ConfigBackup(JSch jsch) {
        this.jsch = jsch;
    }
    public void setRouterIp(String routerIp) {
        this.routerIp = routerIp;
    }

    // Setter for routerPort
    public void setRouterPort(int routerPort) {
        this.routerPort = routerPort;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Method to load properties from config.properties
    private void loadConfig() {
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            // Load properties
            this.routerIp = prop.getProperty("router.ip");
            this.routerPort = Integer.parseInt(prop.getProperty("router.port"));
            this.username = prop.getProperty("router.username");
            this.password = prop.getProperty("router.password");
            this.loginCommand = prop.getProperty("commands.login");
            this.enableCommand = prop.getProperty("commands.enable");
            this.terminalCommand = prop.getProperty("commands.terminal");
            this.configureCommand = prop.getProperty("commands.configure");
            this.backupPath = prop.getProperty("backup.path");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void backupConfig(String backupFileName) {
    	JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        
        String[] confs = {"username", "version", "hostname","domain name", "profile", "ip access list"};
        
        // Generate the timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());

        // Concatenate the timestamp to the backup file name
        String fullBackupFileName = backupFileName + "_" + timestamp + ".txt";
        
    	String backupDir = "C:\\Users\\Administrator\\Documents\\BackupConf";
    	
        File backupFile = new File(backupDir ,fullBackupFileName);
        
        // Create the directories if they don't exist
        new File(backupDir).mkdirs();
        
        try ( FileOutputStream outputStream = new FileOutputStream(backupFile);
        	  PrintWriter printwriter = new PrintWriter(outputStream)) {
             
            // Create the new file
        	backupFile.createNewFile();
        	
        	// Proceed with the existing logic
            session = jsch.getSession(username, routerIp, routerPort);
            session.setPassword(password);
            
         // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            channel = session.openChannel("shell");
            
           //to read data from channel
            
            
            channel.setOutputStream(System.out);

            channel.connect();

            OutputStream out = channel.getOutputStream();
            PrintStream ps = new PrintStream(out, true);
            
            
           ps.println("enable");
          
           
           InputStream in = channel.getInputStream(); 
           
        // Read the output from the router and write it to the file
           BufferedReader reader = new BufferedReader(new InputStreamReader(in));
           String line;
        // Loop through the commands and capture the output
           for (String conf : confs) {
               ps.println("show running-config | include " + conf);

               while ((line = reader.readLine()) != null) {
                   // Skip lines that contain the prompt
                   if (!line.contains("harsha#")) {
                       System.out.println(line);
                       printwriter.println(line);  // Write the line to the file
                   }
                   // Exit the loop after capturing the output for the command
                   if (line.contains(conf)) {
                       break;
                   }
               }
           }
           

           
//        // Execute the additional command and capture all output lines
//           ps.println("show ip int br");
//
//           StringBuilder ipIntOutput = new StringBuilder();
//
//           while ((line = reader.readLine()) != null) {
//               if (line.contains("harsha#")) { // Assuming "harsha>" is the prompt to signify the end of output
//                   break;
//               }
//               ipIntOutput.append(line).append(System.lineSeparator());
//           }

           // Write the captured output to the file
           //printwriter.println(ipIntOutput.toString());
            
            
            
            
            System.out.println("File created and content copied successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
