package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class PostgresReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public JdbcPagingItemReader<Widget> widgetReader(DataSource dataSource) {
        var reader = new JdbcPagingItemReader<Widget>();
        reader.setDataSource(dataSource);
        reader.setPageSize(500);
        reader.setRowMapper(widgetRowMapper());
        reader.setQueryProvider(queryProvider());
        reader.setParameterValues(Map.of("createdAfter", OffsetDateTime.now().minusMinutes(60)));
        return reader;
    }

    private PagingQueryProvider queryProvider() {
        var provider = new PostgresPagingQueryProvider();
        provider.setSelectClause("SELECT id, name, meta, created_at");
        provider.setFromClause("FROM widgets");
        provider.setWhereClause("WHERE created_at >= :createdAfter");
        provider.setSortKeys(Map.of("id", Order.ASCENDING));
        return provider;
    }

    private RowMapper<Widget> widgetRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            UUID id = (UUID) rs.getObject("id");
            String name = rs.getString("name");
            Instant createdAt = rs.getTimestamp("created_at").toInstant();
            String metaJson = rs.getString("meta");
            Map<String, Object> meta = null;
            try {
                meta = objectMapper.readValue(metaJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new Widget(id, name, createdAt, meta);
        };
    }

}
