package com.example.nms.config;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Component
public class ConfigPush {
    private String routerIp; // Router IP address
    private int routerPort; // Router port (e.g., 23 for Telnet)
    private String username; // Username for the router
    private String password; // Password for the router
    private String loginCommand; // Command to send the username
    private String enableCommand; // Command to enter enable mode
    private String terminalCommand; // Command to set terminal length
    private String configureCommand; // Command to enter configuration mode
    private String configFilePath; // Path to the configuration file to push
    private JSch jsch;
    // Constructor to initialize the properties from config file
    public ConfigPush() {
        loadConfig();
    }
   

    // Constructor to accept JSch instance
    public ConfigPush(JSch jsch) {
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
            this.configFilePath = prop.getProperty("config.file.path");
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Boolean pushConfig(String[] commands) {
    	JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        
        try {
            // Create or overwrite the configuration file with the commands
            File configFile = new File("C:\\Users\\Administrator\\Documents\\projectfiles\\configFilePath.txt");
            if (!configFile.exists()) {
                configFile.createNewFile(); // Ensure the file exists
            }
            
            // Write commands to the configuration file using BufferedWriter
            bufferedWriter = new BufferedWriter(new FileWriter(configFile));
            for (String command : commands) {
                bufferedWriter.write(command);
                bufferedWriter.newLine(); // Write each command on a new line
            }
            bufferedWriter.flush(); // Ensure all data is written
            System.out.println("Commands written to file: " + configFilePath);

           // Proceed with the existing logic
            session = jsch.getSession(username, routerIp, routerPort);
            session.setPassword(password);

            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(20000);
            
            channel = session.openChannel("shell");

            // Read the file using BufferedReader
            bufferedReader = new BufferedReader(new FileReader(configFile));
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();

            OutputStream out = channel.getOutputStream();
            PrintStream ps = new PrintStream(out, true);
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                ps.println(line);
                System.out.println("Sending command: " + line);
            }

           
            System.out.println("Commands sent to router.");
            // Give some time for commands to execute
            Thread.sleep(5000);
            
            

        } catch (IOException e) {
            System.err.println("Error with file operations: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("Error closing BufferedWriter: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.err.println("Error closing BufferedReader: " + e.getMessage());
                    e.printStackTrace();
                }
            }
           if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
		return true;
    }
    
 /*// Method to push the configuration to the router
    public void pushConfig(String[] commands) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        FileWriter fileWriter = null;
        
        try {
        	
        	File configFile = new File(configFilePath);
            fileWriter = new FileWriter(configFile);
            fileWriter.write(commands);
            System.out.println(configFilePath);
            fileWriter.flush();
            
            session = jsch.getSession(username, routerIp, routerPort);
            session.setPassword(password);

            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
           
            // Load the configuration file from resources
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath);
                 BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream))) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Configuration file " + configFilePath + " not found in resources.");
                }

                OutputStream out = channel.getOutputStream();
                PrintStream ps = new PrintStream(out, true);

                channel.connect();

                ps.println("enable");
                ps.println("configure terminal");
                
                String line;
                while ((line = fileReader.readLine()) != null) {
                    ps.println(line);
                    System.out.println(line);
                }

                ps.println("end");
                ps.println("write memory");
                ps.println("exit");
                System.out.println(commands);
                // Give some time for commands to execute
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

   /* // Method to push the configuration to the router
    public void pushConfig() {
    	JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        
        try {
            session = jsch.getSession(username, routerIp, routerPort);
            session.setPassword(password);

            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            System.out.println(configFilePath);
            // Send commands to the router
            try (BufferedReader fileReader = new BufferedReader(new FileReader(configFilePath))) {
                System.out.println("hi hello how are u");
            	String line;
                OutputStream out = channel.getOutputStream();
                PrintStream ps = new PrintStream(out, true);

                channel.connect();

                ps.println("enable");
                ps.println("configure terminal");

                while ((line = fileReader.readLine()) != null) {
                    ps.println(line);
                }

                ps.println("end");
                ps.println("write memory");
                ps.println("exit");

                // Give some time for commands to execute
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }*/
    	
//        try (Socket socket = new Socket(routerIp, routerPort);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
//
//            // Send login command
//            writer.println(username);
//            writer.flush();
//            Thread.sleep(1000); // Wait for the router to prompt for password
//
//            // Send password
//            writer.println(password);
//            writer.flush();
//            Thread.sleep(1000); // Wait for password acceptance
//
//            // Navigate to enable mode
//            writer.println(enableCommand);
//            writer.flush();
//            Thread.sleep(1000); // Wait for enable mode
//
//            // Set terminal length
//            writer.println(terminalCommand);
//            writer.flush();
//            Thread.sleep(1000); // Wait for command execution
//
//            // Enter configuration mode
//            writer.println(configureCommand);
//            writer.flush();
//            Thread.sleep(1000); // Wait for config mode
//
//            // Push the configuration from the file
//            try (BufferedReader fileReader = new BufferedReader(new FileReader(configFilePath))) {
//                String line;
//                while ((line = fileReader.readLine()) != null) {
//                    writer.println(line);
//                    writer.flush();
//                }
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }