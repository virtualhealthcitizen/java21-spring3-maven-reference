package org.squidmin.java.spring.maven.gcs.util;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squidmin.java.spring.maven.gcs.GcsModuleTestUtil;
import org.squidmin.java.spring.maven.gcs.entity.ExampleEntity;

import java.util.List;

public class AvroUtilTest {

    private AvroUtil avroUtil;

    @BeforeEach
    void beforeEach() {
        avroUtil = new AvroUtil();
    }

    @Test
    void getAvroSchema() throws Exception {
        String schemaStr = avroUtil.getAvroSchema();
        Assertions.assertNotNull(schemaStr);
        Schema schema = new Schema.Parser().parse(schemaStr);
        Assertions.assertEquals(5, schema.getFields().size());
    }

    @Test
    void serializeToAvro_shouldSerializeCorrectly() throws Exception {
        ExampleEntity item = new ExampleEntity("123", "2023-01-01", "2023-01-02", "valA", "valB");
        List<ExampleEntity> items = List.of(item);

        byte[] avroBytes = avroUtil.serializeToAvro(items);

        Assertions.assertNotNull(avroBytes);
        Schema schema = new Schema.Parser().parse(GcsModuleTestUtil.getAvroSchema("avro/schema.json"));
        Assertions.assertEquals(5, schema.getFields().size());
    }

}
