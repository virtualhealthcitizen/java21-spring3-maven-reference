package org.squidmin.java.spring.maven.gcs;

import org.apache.logging.log4j.util.Strings;
import org.squidmin.java.spring.maven.gcs.service.impl.GcsServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GcsModuleTestUtil {

    public static String getAvroSchema(String schemaPath) throws IOException {
        String schema = Strings.EMPTY;
        try (InputStream is = GcsServiceImpl.class.getClassLoader().getResourceAsStream(schemaPath)) {
            if (is != null) {
                schema = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return schema;
    }

}
