package org.example.discovery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Util {
    static public String randomName(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("Myapp-");
        sb.append(random.nextInt(899)+100);
        sb.append("-");
        sb.append(random.nextInt(899)+100);
        sb.append("-");
        sb.append(random.nextInt(8999)+1000);
        return sb.toString();
    }
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    static public void printLog(Date date, String id, String txrx, String message, int seq, String sender){
        StringBuffer sb = new StringBuffer();
        sb.append(sdf.format(date).toString());
        sb.append(" ");
        sb.append(id);
        sb.append(" ");
        sb.append(txrx);
        sb.append(" ");
        sb.append(message);
        sb.append(" ");
        sb.append("SEQ:");
        sb.append(message);
        sb.append(" ");
        sb.append("SENDER:");
        sb.append(sender);

        System.out.println(sb.toString());
    }
}
