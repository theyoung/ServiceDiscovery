package org.example.discovery.jms;

import javax.jms.*;

public abstract class Messenger {
    Connection connection;
    final private String topicName;
    Session session;
    Topic topic;
    private volatile boolean isConnected;

    public Messenger(Connection connection, String topicName){
        this.connection = connection;
        this.topicName = topicName;
        this.isConnected = false;
    }
    protected boolean setSession(boolean transacted, int ackMode) throws Exception{
        this.session = connection.createSession(transacted, ackMode);
        return this.session != null;
    };
    protected boolean setTopic() throws Exception{
        this.topic = this.session.createTopic(this.topicName);
        return this.topic != null;
    }


    protected void disconnect() throws Exception{
        this.session.close();
    }
}
