package org.squidmin.java.spring.maven.batch.io;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.batch.domain.NormalizedOrder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 */
@Component
public class OrderItemWriter {

    private final JpaItemWriter<NormalizedOrder> jpaWriter;
    private final FlatFileItemWriter<NormalizedOrder> csvWriter;

    public OrderItemWriter(EntityManagerFactory emf,
                           @Value("${export.dir:./exports}") String exportDir) throws Exception {
        this.jpaWriter = new JpaItemWriter<>();
        this.jpaWriter.setEntityManagerFactory(emf);

        Files.createDirectories(Path.of(exportDir));
        this.csvWriter = csvWriter(exportDir + "/normalized_orders.csv");
    }

    private FlatFileItemWriter<NormalizedOrder> csvWriter(String path) throws Exception {
        var writer = new FlatFileItemWriter<NormalizedOrder>();
        writer.setResource(new FileSystemResource(path));
        writer.setShouldDeleteIfExists(false);
        writer.setAppendAllowed(true);

        var extractor = new BeanWrapperFieldExtractor<NormalizedOrder>();
        extractor.setNames(new String[]{"id", "externalOrderId", "email", "currency", "amount", "createdAt", "cancelled"});

        var aggregator = new DelimitedLineAggregator<NormalizedOrder>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        writer.setLineAggregator(aggregator);
        writer.afterPropertiesSet();
        return writer;
    }

    /**
     * Composite writer: persist to DB AND append to CSV export.
     */
    @Bean
    public ItemWriter<NormalizedOrder> normalizedOrderCompositeWriter() {
        return chunk -> {
            List<? extends NormalizedOrder> items = chunk.getItems();
            jpaWriter.write(chunk);
            csvWriter.write(chunk);
        };
    }

}
