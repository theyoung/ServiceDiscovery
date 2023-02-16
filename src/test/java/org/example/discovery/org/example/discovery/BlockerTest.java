package org.example.discovery.org.example.discovery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class BlockerTest {
    /*
    solace.java.host=tcps://mr-connection-6f7gvzle30x.messaging.solace.cloud:55443
solace.java.msg-vpn=pingpong
solace.java.client-username=solace-cloud-client
solace.java.client-password=3giqq3h6f6jrgo34hhcc4v13mu
     */
    String host = "tcps://mr-connection-6f7gvzle30x.messaging.solace.cloud:55443";
    String vpn = "pingpong";
    String username = "solace-cloud-client";
    String password = "3giqq3h6f6jrgo34hhcc4v13mu";

    @Test
    void test_blocker_connect() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Blocker.HOST,this.host);
        properties.setProperty(Blocker.VPN,this.vpn);
        properties.setProperty(Blocker.USER,this.username);
        properties.setProperty(Blocker.PW,this.password);

        Blocker blocker = new Blocker(properties);
        Assertions.assertTrue(blocker.connect());
        blocker.disconnect();
    }
}