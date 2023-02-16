package org.example.discovery.model;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class TraceMessage<T> implements MessageDTO{

    private Class<T> type;
    private T object;

    public TraceMessage(Class<T> type, T object){
        this.type =  type;
        this.object = object;
    }

    private Class<T> getGenericType(){
        return type;
    }

    public List<Field> getFields(){
        List<Field> fields = new ArrayList<>();

        Field[] fs = type.getDeclaredFields();

        for(Field field : fs){
            fields.add(field);
        }

        return fields;
    }

    @Override
    public Message getMessage(Session session) throws Exception{
        MapMessage map =  session.createMapMessage();

        List<Field> fields = getFields();

        for(Field f : fields){
            Object obj = f.get(object);
            if(obj==null) continue;
            if (String.class.getTypeName().equals(f.getType().getName())) {
                map.setString(f.getName(), (String)f.get(object));
            } else if (Integer.class.getTypeName().equals(f.getType().getName())) {
                map.setInt(f.getName(), (Integer)f.get(object));
            } else if (Boolean.class.getTypeName().equals(f.getType().getName())) {
                map.setBoolean(f.getName(), (Boolean) f.get(object));
            } else if (Long.class.getTypeName().equals(f.getType().getName())) {
                map.setLong(f.getName(), (Long)f.get(object));
            }
        }
        map.setString("clazz", type.getName());
        return map;
    }
}
