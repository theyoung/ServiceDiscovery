package org.example.discovery.model;

import javax.jms.*;

public class Publisher extends Messenger{
    private MessageProducer producer;

    public Publisher(Connection connection, String topicName) {
        super(connection, topicName);
    }

    private void createPublisher() throws JMSException {
        this.producer = this.session.createProducer(this.topic);
    }
    public boolean initPublisher(boolean transacted, int ackmode) throws Exception{
        this.setSession(transacted, ackmode);
        this.setTopic();
        this.createPublisher();

        return producer != null;
    }

    public void sendMessage(MessageDTO message) throws Exception{
        if(this.producer == null) throw new JMSException("Producer Not Init Yet");
//        Message msg = message.getMessage(this.session);
        Message msg = this.session.createTextMessage("abc");

        this.producer.send(msg, DeliveryMode.PERSISTENT,Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
    }

    @Override
    protected void disconnect() throws Exception {
        if(producer != null) producer.close();
        super.disconnect();
    }
}
