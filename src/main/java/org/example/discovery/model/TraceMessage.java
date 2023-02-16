package org.example.discovery.model;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

public class TraceMessage<V> implements MessageDTO{

//    public TraceMessage(){
//        map = new HashMap<>();
//    }





    @Override
    public Message getMessage(Session session) throws Exception{
        MapMessage message = session.createMapMessage();
//        for(Map.Entry<String,V> entry : map.entrySet()){
//            if(entry.getValue() instanceof Integer){
//                message.setInt(entry.getKey(), (int)entry.getValue());
//            } else if(entry.getValue() instanceof String){
//                message.setString(entry.getKey(), (String)entry.getValue());
//            } else if(entry.getValue() instanceof Boolean){
//                message.setBoolean(entry.getKey(), (boolean)entry.getValue());
//            }
//        }

        return message;
    }
}
