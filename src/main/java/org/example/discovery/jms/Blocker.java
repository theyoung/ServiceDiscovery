package org.example.discovery.jms;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.discovery.Util;
import org.example.discovery.model.PingPong;

import javax.jms.Connection;
import javax.jms.Session;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class Blocker {
    public static final String HOST = "solace.java.host";
    public static final String VPN = "solace.java.msg-vpn";
    public static final String USER = "solace.java.client-username";
    public static final String PW = "solace.java.client-password";
    public static final Log logger = LogFactory.getLog(Blocker.class);
    public static final String id = Util.randomName();
    public static AtomicInteger seq = new AtomicInteger(0);

    Properties props;

    //test때문에 public으로 한다. 향후 private로 변경해야 한다.
    public Connection connection;
    volatile boolean isConnected = false;

    public Blocker(Properties properties) {
        this.props = properties;
    }

    public boolean isConnected(){
        return this.isConnected;
    }

    public Publisher createPublisher(String topic) throws Exception{
        Publisher publisher = new Publisher(this.connection, topic);
        publisher.initPublisher(false, Session.AUTO_ACKNOWLEDGE);
        return publisher;
    }

    public Subscriber createSubscriber(String topic) throws Exception{
        Subscriber subscriber = new Subscriber(this.connection, topic);
        subscriber.initSubscriber(false,Session.AUTO_ACKNOWLEDGE);
        return subscriber;
    }

    public void subscribeStart() throws Exception{
        this.connection.start();
    }

    public boolean connect() throws Exception {
        logger.debug("Connecting... to Solace Broker");
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(props.getProperty(Blocker.HOST));
        connectionFactory.setVPN(props.getProperty(Blocker.VPN));
        connectionFactory.setUsername(props.getProperty(Blocker.USER));
        connectionFactory.setPassword(props.getProperty(Blocker.PW));

        connection = connectionFactory.createConnection();
        if(connection != null){
            //exception handling
            connection.setExceptionListener((exception)->{
                logger.info("Exception of Connection");
                exception.printStackTrace();
                this.disconnect();
            });
        }
        if(connection!=null) this.isConnected = true;
        return connection != null;
    }

    //only for listener
    public void startSubscription() throws Exception{
        if(this.isConnected) connection.start();
    }

    //disconnect의 결과는 알수 없다
    public void disconnect(){
        this.isConnected = false;
        if(connection == null) return;
        try{
            connection.stop();
            connection.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
