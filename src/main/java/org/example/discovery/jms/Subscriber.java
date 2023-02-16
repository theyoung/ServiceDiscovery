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
        this.consumer = this.session.createConsumer(this.topic);

    }
    public boolean initSubscriber(boolean transacted, int ackmode) throws Exception{
        this.setSession(transacted, ackmode);
        this.setTopic();
        this.createSubscriber();

        return this.consumer != null;
    }

    public void registeListener(MessageListener listener) throws JMSException{
        if(this.consumer == null) throw new JMSException("Subscriber Not Init Yet");
        this.consumer.setMessageListener(listener);
    }

    @Override
    protected void disconnect() throws Exception {
        if(consumer != null) consumer.close();
        super.disconnect();
    }

    public void test() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("myapp/ping");
        MessageConsumer messageConsumer = session.createConsumer(topic);

        messageConsumer.setMessageListener((Message message)->{
            try{
                logger.debug("Ping Received Successes!");
                if(message instanceof MapMessage){

                    MapMessage map = (MapMessage) message;
                    String sender = map.getString("sender");
                    String msg = map.getString("msg");
                    String seq = map.getString("seq");
                    logger.debug("Ping Received Successes!" + seq + " " + sender + " " + msg);

                }
            } catch (JMSException exception){
                exception.printStackTrace();
            }
        });
        this.connection.start();

    }
}
