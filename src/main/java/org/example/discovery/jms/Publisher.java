package org.example.discovery.jms;

import org.example.discovery.model.MessageDTO;

import javax.jms.*;

public class Publisher extends Messenger{
    private MessageProducer producer;
    volatile boolean isConnected = false;
    public Publisher(Connection connection, String topicName) {
        super(connection, topicName);
    }

    private void createPublisher() throws JMSException {
        this.producer = this.session.createProducer(this.topic);
        isConnected = true;
    }
    public boolean initPublisher(boolean transacted, int ackmode) throws Exception{
        this.setSession(transacted, ackmode);
        this.setTopic();
        this.createPublisher();

        return producer != null;
    }

    public void sendMessage(MessageDTO message) throws Exception{
        if(this.producer == null) throw new JMSException("Producer Not Init Yet");
        if(!this.isConnected) return;
        Message msg = message.getMessage(this.session);

        this.producer.send(msg, DeliveryMode.PERSISTENT,Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
    }

    @Override
    public void disconnect() throws Exception {
        if(isConnected && session != null) session.unsubscribe(this.topic.getTopicName());

        isConnected = false;
        if(producer != null) producer.close();
        super.disconnect();
    }
}
