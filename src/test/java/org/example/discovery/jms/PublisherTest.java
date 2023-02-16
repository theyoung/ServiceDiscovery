package org.example.discovery.jms;


import org.example.discovery.model.PingPong;
import org.example.discovery.model.TraceMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.util.Date;
import java.util.Properties;


class PublisherTest {
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
    void test_publish_message() throws Exception {

        Subscriber subscriberPing = new Subscriber(this.blocker.connection, "myapp/ping");
        subscriberPing.initSubscriber(false,Session.AUTO_ACKNOWLEDGE);
        subscriberPing.registerListener((revMessage)->{
            try{
                if(revMessage instanceof MapMessage){
                    MapMessage msg = (MapMessage) revMessage;
                    String clazz = msg.getString("clazz");
                    if(PingPong.class.getName().equals(clazz)){
                        PingPong pin = PingPong.getPingPongByMapMessage(msg);
                        System.out.println(pin.toString());
                    }
                    try {
                        System.out.println(revMessage.getJMSMessageID());
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        Subscriber subscriberPong = new Subscriber(this.blocker.connection, "myapp/pong");
        subscriberPong.initSubscriber(false,Session.AUTO_ACKNOWLEDGE);
        subscriberPong.registerListener((revMessage)->{
            try{
                if(revMessage instanceof MapMessage){
                    MapMessage msg = (MapMessage) revMessage;
                    String clazz = msg.getString("clazz");
                    if(PingPong.class.getName().equals(clazz)){
                        PingPong pin = PingPong.getPingPongByMapMessage(msg);
                        System.out.println(pin.toString());
                    }
                    try {
                        System.out.println(revMessage.getJMSMessageID());
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        });
        this.blocker.startSubscription();

        Publisher publisherPing = new Publisher(this.blocker.connection, "myapp/ping");
        publisherPing.initPublisher(false, Session.AUTO_ACKNOWLEDGE);
        PingPong p1 = new PingPong();
        p1.setSeq(1);
        p1.setMessage("PING");
        p1.setTimestamp(new Date().getTime());
        p1.setOriginSender("SEND1234");
        p1.setLastSender("SEND1234");

        TraceMessage<PingPong> messagePing = new TraceMessage<>(PingPong.class, p1);
        publisherPing.sendMessage(messagePing);

        PingPong p2 = new PingPong();
        p2.setSeq(2);
        p2.setMessage("PONG");
        p2.setTimestamp(new Date().getTime());
        p2.setOriginSender("SEND1234");
        p2.setLastSender("SEND1234");

        Publisher publisherPong = new Publisher(this.blocker.connection, "myapp/pong");
        publisherPong.initPublisher(false, Session.AUTO_ACKNOWLEDGE);
        TraceMessage<PingPong> messagePong = new TraceMessage<>(PingPong.class, p2);
        publisherPong.sendMessage(messagePong);

        Thread.sleep(1000 * 5);



    }

}