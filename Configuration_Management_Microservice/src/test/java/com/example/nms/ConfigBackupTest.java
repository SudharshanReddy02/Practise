package com.example.nms;

import com.example.nms.config.ConfigBackup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class ConfigBackupTest {

    @Test
    void testBackupConfig() {
        // Arrange
        ConfigBackup configBackup = new ConfigBackup();
        String filename = "testBackup.txt";

        // Act & Assert
        assertDoesNotThrow(() -> configBackup.backupConfig(filename));
    }
}