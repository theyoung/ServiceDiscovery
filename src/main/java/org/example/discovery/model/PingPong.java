package org.example.discovery.model;

import javax.jms.MapMessage;
import java.sql.Timestamp;

public class PingPong {
    Integer seq;
    String message;
    Long timestamp;
    String originSender;
    String lastSender;

    public PingPong(){

    }

    public String getOriginSender() {
        return originSender;
    }

    public void setOriginSender(String originSender) {
        this.originSender = originSender;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    static public PingPong getPingPongByMapMessage(MapMessage msg) throws Exception{
        PingPong ping = new PingPong();
        ping.setMessage(msg.getString("message"));
        ping.setSeq(msg.getInt("seq"));
        ping.setTimestamp(msg.getLong("timestamp"));
        ping.setOriginSender(msg.getString("originSender"));
        ping.setLastSender(msg.getString("lastSender"));
        return ping;
    }


    @Override
    public String toString() {
        return "PingPong{" +
                "seq=" + seq +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", originSender='" + originSender + '\'' +
                ", lastSender='" + lastSender + '\'' +
                '}';
    }


}
