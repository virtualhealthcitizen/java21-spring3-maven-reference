package org.squidmin.java.spring.maven.batch.io;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ExecutionContext;
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
        this.csvWriter = createCsvWriter(exportDir + "/normalized_orders.csv");
    }

    private FlatFileItemWriter<NormalizedOrder> createCsvWriter(String path) throws Exception {
        FlatFileItemWriter<NormalizedOrder> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(path));
        writer.setShouldDeleteIfExists(true); // Ensure a clean slate for each job run
        writer.setAppendAllowed(false); // Avoid appending to an existing file

        BeanWrapperFieldExtractor<NormalizedOrder> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "externalOrderId", "email", "currency", "amount", "createdAt", "cancelled"});

        DelimitedLineAggregator<NormalizedOrder> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        writer.setLineAggregator(aggregator);
        writer.afterPropertiesSet(); // Ensure the writer is fully initialized
        writer.open(new ExecutionContext()); // Open the writer to prepare for writing

        System.out.println("CSV Writer Path: " + path);

        return writer;
    }

    @Bean
    public ItemWriter<NormalizedOrder> normalizedOrderCompositeWriter() {
        return items -> {
            jpaWriter.write(items); // Write to the database
            csvWriter.write(items); // Write to the CSV file
        };
    }

}
