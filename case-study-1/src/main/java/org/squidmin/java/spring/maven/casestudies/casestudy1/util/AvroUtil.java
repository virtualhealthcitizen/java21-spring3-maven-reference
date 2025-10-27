package org.squidmin.java.spring.maven.casestudies.casestudy1.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AvroUtil {

    public String getAvroSchema() throws IOException {
        String schema = Strings.EMPTY;
        try (InputStream is = AvroUtil.class.getClassLoader().getResourceAsStream("avro/schema.json")) {
            if (is != null) {
                schema = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return schema;
    }

    public byte[] serializeToAvro(List<Widget> items) throws IOException {
        Schema schema = new Schema.Parser().parse(getAvroSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(schema, out);

        for (Widget item : items) {
            GenericRecord record = new GenericData.Record(schema);
            record.put("id", item.getId());
            record.put("createdAt", item.getCreatedAt());
            record.put("updatedAt", item.getUpdatedAt());
            record.put("name", item.getName());
            record.put("meta", item.getMeta());
            dataFileWriter.append(record);
        }

        dataFileWriter.close();
        return out.toByteArray();
    }

}
