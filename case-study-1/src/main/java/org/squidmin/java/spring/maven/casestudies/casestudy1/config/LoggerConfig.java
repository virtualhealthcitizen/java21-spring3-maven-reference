package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import org.slf4j.Logger;

public class LoggerConfig {

    public interface LoggerAware {
        void setLogger(Logger logger);
    }

}
