package org.squidmin.java.spring.maven.casestudies.casestudy1.util;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class LoggerAspect {

    @Before(value = "@within(org.squidmin.java.spring.maven.casestudies.casestudy1.util.Loggable) && target(bean)")
    public void injectLogger(Object bean) {
        Class<?> clazz = bean.getClass();
        try {
            Field logField = clazz.getDeclaredField("log");
            if (logField != null && Logger.class.isAssignableFrom(logField.getType())) {
                logField.setAccessible(true);
                Logger logger = LoggerFactory.getLogger(clazz);
                logField.set(bean, logger);
            }
        } catch (NoSuchFieldException ignored) {
            // No field named 'log', skip injection
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject logger into " + clazz.getName(), e);
        }
    }
}
