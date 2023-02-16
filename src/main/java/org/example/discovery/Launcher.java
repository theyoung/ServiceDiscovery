package org.example.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.discovery.jms.Blocker;
import org.example.discovery.jms.Publisher;
import org.example.discovery.jms.Subscriber;
import org.example.discovery.model.PingPong;
import org.example.discovery.model.TraceMessage;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.management.JMException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Launcher {
    static final Log logger = LogFactory.getLog(Launcher.class);
    static private Launcher launcher;
    Properties props;
    private Blocker blocker;

    static public Launcher getLauncher(){
        if(Launcher.launcher == null){
            synchronized(Launcher.class){
                Launcher.launcher = new Launcher();
            }
        }

        return Launcher.launcher;
    }

    private Launcher(){
        logger.debug("Launcher Initialized");
        this.props = new Properties();
    }

    public void start(String[] args, String config) throws Exception{
        this.props.putAll(LoadProperties.load(config));
        this.props.putAll(LoadProperties.load(args));
        this.blocker = new Blocker(this.props);
        if(!this.blocker.connect()){
            throw new JMException("Connection Failed");
        }
    }

    public Publisher createPublisher(String topic) throws Exception{
        if(!this.blocker.isConnected()) throw new Exception("Publish fail, Connection First");
        return this.blocker.createPublisher(topic);
    }
    public Subscriber createSubscriber(String topic) throws Exception{
        if(!this.blocker.isConnected()) throw new Exception("Publish fail, Connection First");
        return this.blocker.createSubscriber(topic);
    }

    public void subscribeStart() throws Exception{
        this.blocker.subscribeStart();
    }
    public boolean isConnected(){
        return this.blocker.isConnected();
    }

    public void stopAll(){
        this.blocker.disconnect();
    }

    public int getTimeout(){
        return props.getProperty("timeout") == null ? 10 : Integer.parseInt(props.getProperty("timeout")) ;
    }
    public int getInterval(){
        return props.getProperty("interval") == null ? 0 : Integer.parseInt(props.getProperty("interval"));
    }
    public void disconnect(){
        this.blocker.disconnect();
    }

    static final String TOPIC_PING = "myapp/ping";
    static final String TOPIC_PONG = "myapp/pong";
    static final String MSG_PING = "PING";
    static final String MSG_PONG = "PONG";
    static final String MSG_TX = "TX";
    static final String MSG_RX = "RX";

    public static void main(String[] args) throws Exception {
        Launcher launcher = Launcher.getLauncher();
        launcher.start(args, "config.properties");

        while(!launcher.isConnected()){
            Thread.sleep(0);
        }

        final Publisher pingPublisher = launcher.createPublisher(Launcher.TOPIC_PING);
        final Publisher pongPublisher = launcher.createPublisher(Launcher.TOPIC_PONG);
        final Subscriber pingSubscriber = launcher.createSubscriber(Launcher.TOPIC_PING);
        final Subscriber pongSubscriber = launcher.createSubscriber(Launcher.TOPIC_PONG);


        pingSubscriber.registerListener((revMessage)->{
            try{
                if(revMessage instanceof MapMessage){
                    MapMessage msg = (MapMessage) revMessage;
                    String clazz = msg.getString("clazz");
                    if(PingPong.class.getName().equals(clazz)) {
                        PingPong pin = PingPong.getPingPongByMapMessage(msg);

                        Util.printLog(new Date(), Blocker.id, MSG_RX, MSG_PING, pin.getSeq(), pin.getLastSender());


                        PingPong pong = new PingPong();
                        int seq = pin.getSeq();
                        pong.setSeq(seq);
                        pong.setMessage(MSG_PONG);
                        pong.setTimestamp(new Date().getTime());
                        pong.setOriginSender(pin.getOriginSender());
                        pong.setLastSender(Blocker.id);

                        TraceMessage<PingPong> pongMessage = new TraceMessage<>(PingPong.class, pong);
                        pongPublisher.sendMessage(pongMessage);
                        Util.printLog(new Date(), Blocker.id, MSG_TX, MSG_PING, seq, Blocker.id);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        pongSubscriber.registerListener((revMessage)->{
            try{
                if(revMessage instanceof MapMessage){
                    MapMessage msg = (MapMessage) revMessage;
                    String clazz = msg.getString("clazz");
                    if(PingPong.class.getName().equals(clazz)){
                        PingPong pin = PingPong.getPingPongByMapMessage(msg);
                        Util.printLog(new Date(), Blocker.id, MSG_RX, MSG_PONG, pin.getSeq(), pin.getLastSender());
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        launcher.subscribeStart();

        int inteval = launcher.getInterval();
        int timeout = launcher.getTimeout();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Future pingFuture = null;
        if(0 < inteval) {
            pingFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
                try {
                    PingPong ping = new PingPong();
                    int seq = Blocker.seq.getAndIncrement();
                    ping.setSeq(seq);
                    String message = "PING";
                    ping.setMessage(message);
                    ping.setTimestamp(new Date().getTime());
                    ping.setOriginSender(Blocker.id);
                    ping.setLastSender(Blocker.id);

                    TraceMessage<PingPong> pingMessage = new TraceMessage<>(PingPong.class, ping);
                    pingPublisher.sendMessage(pingMessage);
                    Util.printLog(new Date(), Blocker.id, MSG_TX, MSG_PING, seq, Blocker.id);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.fatal("Ping Message Publisher Broken");
                }
            }, 0, launcher.getInterval(), TimeUnit.SECONDS);


        }
        Future finalPingFuture = pingFuture;
        scheduledExecutorService.schedule(()->{
            try{
                if(finalPingFuture != null) finalPingFuture.cancel(false);
                scheduledExecutorService.shutdown();
                pingPublisher.disconnect();
                pongPublisher.disconnect();
                pingSubscriber.disconnect();
                pongSubscriber.disconnect();
                launcher.disconnect();
            } catch (Exception e){
                e.printStackTrace();
            }
            logger.info("System Terminated");
        }, launcher.getTimeout(), TimeUnit.SECONDS);

    }


}
