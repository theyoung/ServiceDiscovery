package org.example.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class LoadProperties {
    static final Log logger = LogFactory.getLog(LoadProperties.class);

    public static Properties load(String path) throws IOException{
        InputStream stream =  LoadProperties.class.getClassLoader().getResourceAsStream(path);
        Properties props = new Properties();

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            props.load(reader);
        } catch (IOException e) {
            throw e;
        }
        return props;
    }

    public static Properties load(String[] args) throws IOException{
        Properties props = new Properties();
        for (int i = 0; i < args.length; i++) {
            if (i < args.length - 1 && args[i].startsWith("-")) {
                props.setProperty(args[i].substring(1), args[i+1]);
            }
        }
        return props;
    }

}
