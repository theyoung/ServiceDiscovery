package org.example.discovery.model;

import com.solacesystems.jcsmp.Producer;
import org.example.discovery.model.Blocker;
import org.example.discovery.model.Publisher;
import org.example.discovery.model.Subscriber;
import org.example.discovery.model.TraceMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jms.*;
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
        subscriberPing.registeListener((revMessage)->{
            System.out.println(revMessage);
            if(revMessage instanceof MapMessage){
                try {
                    System.out.println(revMessage.getJMSMessageID());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Subscriber subscriberPong = new Subscriber(this.blocker.connection, "myapp/pong");
        subscriberPong.initSubscriber(false,Session.AUTO_ACKNOWLEDGE);
        subscriberPong.registeListener((revMessage)->{
            System.out.println(revMessage);
            if(revMessage instanceof MapMessage){
                try {
                    System.out.println(revMessage.getJMSMessageID());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.blocker.startSubscription();

        Publisher publisherPing = new Publisher(this.blocker.connection, "myapp/ping");
        publisherPing.initPublisher(false, Session.AUTO_ACKNOWLEDGE);
        TraceMessage<String> messagePing = new TraceMessage<>();
        messagePing.setValue("seq","1");
        messagePing.setValue("msg","PING");
        messagePing.setValue("sender","SenderID1234");
        publisherPing.sendMessage(messagePing);

        Publisher publisherPong = new Publisher(this.blocker.connection, "myapp/pong");
        publisherPong.initPublisher(false, Session.AUTO_ACKNOWLEDGE);
        TraceMessage<String> messagePong = new TraceMessage<>();
        messagePong.setValue("seq","1");
        messagePong.setValue("msg","Pong");
        messagePong.setValue("sender","SenderID1234");
        publisherPong.sendMessage(messagePong);



        Thread.sleep(1000 * 5);



    }

}