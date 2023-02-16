package org.example.discovery.model;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.discovery.LoadProperties;

import javax.jms.Connection;
import java.util.Properties;

public class Blocker {
    static final String HOST = "solace.java.host";
    static final String VPN = "solace.java.msg-vpn";
    static final String USER = "solace.java.client-username";
    static final String PW = "solace.java.client-password";
    static final Log logger = LogFactory.getLog(Blocker.class);

    Properties props;
    Connection connection;
    volatile boolean isConnected = false;

    public Blocker(Properties properties) {
        this.props = properties;
    }

    public boolean isConnected(){
        return this.isConnected;
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
