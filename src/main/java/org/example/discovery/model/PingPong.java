package org.example.discovery.model;

import java.sql.Timestamp;

public class PingPong {
    Integer seq;
    String message;
    Long timestamp;

    PingPong(int seq, String message, Timestamp timestamp){
        this.seq = seq;
        this.message = message;
        this.timestamp = timestamp.getTime();
    }
}
