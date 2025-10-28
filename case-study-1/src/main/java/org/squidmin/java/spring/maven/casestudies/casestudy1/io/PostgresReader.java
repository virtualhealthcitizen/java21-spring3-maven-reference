package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;

@Component
public class PostgresReader implements ItemReader<Widget> {

    private static final Logger log = LoggerFactory.getLogger(PostgresReader.class);

    @Override
    public Widget read() {
        // Read record from database
        return null;
    }

}
