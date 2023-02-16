package org.example.discovery.model;

import javax.jms.Message;
import javax.jms.Session;

public interface MessageDTO {
    Message getMessage(Session session) throws Exception;

}
