package org.example.discovery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LauncherTest {
    @Test
    @DisplayName("All the params are setted")
    void test_set_params() throws Exception{
        Launcher laun = Launcher.getLauncher();
        laun.start(new String[]{"-timeout","100", "-interval", "2"}, "config.properties");
        Assertions.assertEquals("100", laun.props.getProperty("timeout"));
        Assertions.assertEquals("2", laun.props.getProperty("interval"));
        Assertions.assertEquals("pingpong", laun.props.getProperty("solace.java.msg-vpn"));
        Assertions.assertNotEquals("false", laun.props.getProperty("solace.java.apiProperties.reapply_subscriptions"));
        laun.stopAll();
    }

    @Test
    @DisplayName("connection test")
    void test_connection() throws Exception{
        Launcher laun = Launcher.getLauncher();
        laun.start(new String[]{"-timeout","100", "-interval", "2"}, "config.properties");
        Assertions.assertTrue(laun.isConnected());
        laun.stopAll();
        Assertions.assertFalse(laun.isConnected());

    }
}