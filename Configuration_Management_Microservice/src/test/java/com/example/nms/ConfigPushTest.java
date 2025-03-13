package com.example.nms;

import com.example.nms.config.ConfigPush;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConfigPushTest {

    @Test
    void testPushConfig() {
        // Arrange
        ConfigPush configPush = new ConfigPush();
        String[] testCommands = {"enable","conf t", "hostname gulati"};

        // Act
        try {
            Boolean result = configPush.pushConfig(testCommands);
            // Assert
            assertTrue(result==true);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}