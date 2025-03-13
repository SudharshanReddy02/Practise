package com.example.nms.config;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
public class ConfigChangeTracker {
    private String routerIp; // Router IP address
    private int routerPort; // Router port (e.g., 22 for SSH)
    private String username; // Username for the router
    private String password; // Password for the router
    private String loginCommand; // Command to send the username
    private String enableCommand; // Command to enter enable mode
    private String terminalCommand; // Command to set terminal length
    private String configureCommand; // Command to enter configuration mode
    private String logFilePath; // Path to save the log file
    private JSch jsch;

    // Constructor to initialize the properties from config file
    public ConfigChangeTracker() {
        loadConfig();
        this.jsch = new JSch(); // Initialize JSch instance
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
            this.logFilePath = prop.getProperty("log.file.path");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to fetch logs and save them to a file based on the passed timestamp
    public void fetchLogs(String timestampFilter) {
        Session session = null;
        Channel channel = null;

        // Generate the timestamped log file name
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String fullLogFileName = "logs_" + timestamp + ".txt";

        File logFile = new File(logFilePath, fullLogFileName);

        // Create the directories if they don't exist
        new File(logFile.getParent()).mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(logFile);
             PrintWriter printWriter = new PrintWriter(outputStream)) {

            // Proceed with the existing logic
            session = jsch.getSession(username, routerIp, routerPort);
            session.setPassword(password);

            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = session.openChannel("shell");
            channel.setOutputStream(System.out);
            channel.connect();

            OutputStream out = channel.getOutputStream();
            PrintStream ps = new PrintStream(out, true);

            ps.println(enableCommand); // Enter enable mode
            ps.println("show logging"); // Command to get logs

            InputStream in = channel.getInputStream();

            // Read the output from the router and write filtered logs to the file
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            int logCount = 0;
            /*while ((line = reader.readLine()) != null) {
                if (line.contains(timestampFilter)) { // Filter based on the timestamp
                    printWriter.println(line);
                    printWriter.flush(); // Ensure immediate write
                    logCount++;

                    // Break after fetching 3-4 logs
                    if (logCount >= 1) {
                        break;
                    }
                }
            }*/
            System.out.println("Filtered logs saved successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (JSchException e) {
            System.err.println("An SSH error occurred: " + e.getMessage());
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
}