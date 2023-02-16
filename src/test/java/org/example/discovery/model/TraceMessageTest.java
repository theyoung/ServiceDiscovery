package org.example.discovery.model;

import org.example.discovery.jms.Blocker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class TraceMessageTest {
    String host = "tcps://mr-connection-6f7gvzle30x.messaging.solace.cloud:55443";
    String vpn = "pingpong";
    String username = "solace-cloud-client";
    String password = "3giqq3h6f6jrgo34hhcc4v13mu";

    Blocker blocker;

    @BeforeEach
    void test_blocker_connect() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Blocker.HOST,this.host);
        properties.setProperty(Blocker.VPN,this.vpn);
        properties.setProperty(Blocker.USER,this.username);
        properties.setProperty(Blocker.PW,this.password);

        this.blocker = new Blocker(properties);
        this.blocker.connect();
    }

    @Test
    void test_generic_type() throws Exception {
        long time = new Date().getTime();
        PingPong pingPong = new PingPong();
        pingPong.setSeq(1);
        pingPong.setMessage("test1");
        pingPong.setTimestamp(time);

        TraceMessage<PingPong> traceMessage = new TraceMessage<>(PingPong.class, pingPong);
//        List<Field> list = traceMessage.getFields();
//
//        for(Field field : list){
//            System.out.println(field.getType() + " " + field.getName() + " " + String.class.getTypeName());
//            if (String.class.getTypeName().equals(field.getType().getName())) {
//                System.out.println("Same");
//            }
//        }

//        Integer seq;
//        String message;
//        Long timestamp;
        Message message = traceMessage.getMessage(this.blocker.connection.createSession(false, Session.AUTO_ACKNOWLEDGE));
        MapMessage msg = (MapMessage) message;
        Assertions.assertEquals(1, msg.getInt("seq"));
        Assertions.assertEquals("test1", msg.getString("message"));
        Assertions.assertEquals(time, msg.getLong("timestamp"));
        System.out.println(msg.getString("clazz"));
        String clazz = msg.getString("clazz");

        //역산까지 할 시간이 없다 rev는 수작업 처리
        if (clazz.equals(PingPong.class.getName())) {
            PingPong rev = new PingPong();
            rev.setTimestamp(msg.getLong("timestamp"));
            rev.setSeq(msg.getInt("seq"));
            rev.setMessage(msg.getString("message"));
        }
    }


}