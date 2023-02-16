package org.example.discovery;

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
}
