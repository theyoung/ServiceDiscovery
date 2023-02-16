package org.example.discovery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LoadPropertiesTest {

    @Test
    @DisplayName("Config file load")
    void file_load() throws IOException {
        Properties properties = LoadProperties.load("config.properties");
        Assertions.assertTrue(0 < properties.size());
    }

    @Test
    @DisplayName("arguments load")
    void testLoad() throws IOException {
        Properties properties = LoadProperties.load(new String[]{"-timeout","10", "-interval", "2"});
        Assertions.assertEquals(10, Integer.parseInt(properties.getProperty("timeout")));
        Assertions.assertEquals(2, Integer.parseInt(properties.getProperty("interval")));
    }
}