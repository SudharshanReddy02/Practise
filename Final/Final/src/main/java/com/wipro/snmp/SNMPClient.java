package com.wipro.snmp;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.*;
import java.io.IOException;
public class SNMPClient {
    private static final String SNMP_COMMUNITY = "public";  
    private static final String SNMP_HOST = "172.20.0.2";  
    private static final int SNMP_PORT = 161;  

    private static final String OID_SYS_UPTIME = ".1.3.6.1.2.1.1.3.0";
    private static final String OID_CPU_USAGE = ".1.3.6.1.4.1.9.2.1.58.0"; 
    private static final String OID_MEMORY_USAGE = ".1.3.6.1.4.1.9.9.48.1.1.1.6.1"; 
    private static final String OID_TOTAL_MEMORY = ".1.3.6.1.4.1.9.9.48.1.1.1.5.1";
    
    public static void main(String[] args) {
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            snmp.listen();

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(SNMP_COMMUNITY));
            target.setAddress(new UdpAddress(SNMP_HOST + "/" + SNMP_PORT));
            target.setVersion(SnmpConstants.version2c);
            target.setTimeout(5000);
            target.setRetries(3);

            String systemUptime = formatUptime(getSNMPData(snmp, target, OID_SYS_UPTIME));
            String cpuUsage = getSNMPData(snmp, target, OID_CPU_USAGE) + "%";
            String totalmemory = getSNMPData(snmp, target, OID_TOTAL_MEMORY);
            String memoryUsage = formatMemory(getSNMPData(snmp, target, OID_MEMORY_USAGE), totalmemory);

            System.out.println("SNMP Device System Information:");
            System.out.println("--------------------------------");
            System.out.println("System Uptime: " + systemUptime);
            System.out.println("CPU Usage: " + cpuUsage);
            System.out.println("Memory Usage: " + memoryUsage);

            snmp.close();
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private static String getSNMPData(Snmp snmp, CommunityTarget target, String oid) {
        try {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);

            ResponseEvent responseEvent = snmp.get(pdu, target);

            if (responseEvent != null) {
                PDU responsePDU = responseEvent.getResponse();
                if (responsePDU != null && responsePDU.getErrorStatus() == PDU.noError) {
                    VariableBinding vb = responsePDU.get(0);
                    return vb.getVariable().toString();
                } else {
                    return "Error: " + responsePDU.getErrorStatusText() + ", Error Index: " + responsePDU.getErrorIndex();
                }
            } else {
                return "Error: No response from the SNMP device";
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    private static String formatUptime(String uptime) {
        try {
            String[] parts = uptime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + " hours " + minutes + " minutes";
        } catch (Exception e) {
            return uptime; 
        }
    }

    private static String formatMemory(String memoryUsage, String totalMemory) {
        try {
            long usedMemoryBytes = Long.parseLong(memoryUsage);
            long totalMemoryBytes = Long.parseLong(totalMemory);
            
            if (totalMemoryBytes == 0) {
                return "Error: Total memory cannot be zero";
            }
            
            int percentage = (int) ((usedMemoryBytes * 10) / totalMemoryBytes); 
            return percentage + "%";
        } catch (NumberFormatException e) {
            return "Invalid memory data"; 
        }
    }
}

