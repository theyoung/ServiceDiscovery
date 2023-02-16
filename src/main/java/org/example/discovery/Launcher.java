package org.example.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.discovery.jms.Blocker;

import javax.management.JMException;
import java.util.Properties;

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
    public boolean isConnected(){
        return this.blocker.isConnected();
    }

    public void stopAll(){
        this.blocker.disconnect();
    }
    public static void main(String[] args) throws Exception {
        Launcher laun = Launcher.getLauncher();
        laun.start(args, "config.properties");
    }
}
