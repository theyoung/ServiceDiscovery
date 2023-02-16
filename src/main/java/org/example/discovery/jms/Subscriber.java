package org.example.discovery.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;

public class Subscriber extends Messenger{
    static final Log logger = LogFactory.getLog(Subscriber.class);

    private MessageConsumer consumer;

    public Subscriber(Connection connection, String topicName) {
        super(connection, topicName);
    }

    private void createSubscriber() throws JMSException {
        //내가 보낸 메시지를 무시한다. 다만 queued 되어 있는 메시지는 받게 된다.
        this.consumer = this.session.createDurableSubscriber(this.topic,this.topic.getTopicName(),"",true);
    }

    private void createSubscriberNonDurable() throws JMSException {
        this.consumer = this.session.createConsumer(this.topic);
    }
    public boolean initSubscriber(boolean transacted, int ackmode) throws Exception{
        this.setSession(transacted, ackmode);
        this.setTopic();
        this.createSubscriberNonDurable();

        return this.consumer != null;
    }

    public void registerListener(MessageListener listener) throws JMSException{
        if(this.consumer == null) throw new JMSException("Subscriber Not Init Yet");
        this.consumer.setMessageListener(listener);
    }

    @Override
    public void disconnect() throws Exception {
        if(consumer != null) consumer.close();
        super.disconnect();
    }

}
