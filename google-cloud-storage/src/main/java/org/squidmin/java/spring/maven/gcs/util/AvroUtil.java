package org.squidmin.java.spring.maven.gcs.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.gcs.dto.ExampleUploadItem;
import org.squidmin.java.spring.maven.gcs.service.impl.GcsServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AvroUtil {

    public String getAvroSchema() throws IOException {
        String schema = Strings.EMPTY;
        try (InputStream is = GcsServiceImpl.class.getClassLoader().getResourceAsStream("avro/schema.json")) {
            if (is != null) {
                schema = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return schema;
    }

    public byte[] serializeToAvro(List<ExampleUploadItem> items) throws IOException {
        Schema schema = new Schema.Parser().parse(getAvroSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(schema, out);

        for (ExampleUploadItem item : items) {
            GenericRecord record = new GenericData.Record(schema);
            record.put("id", item.getId());
            record.put("creationTimestamp", item.getCreationTimestamp());
            record.put("lastUpdateTimestamp", item.getLastUpdateTimestamp());
            record.put("columnA", item.getColumnA());
            record.put("columnB", item.getColumnB());
            dataFileWriter.append(record);
        }

        dataFileWriter.close();
        return out.toByteArray();
    }

}
