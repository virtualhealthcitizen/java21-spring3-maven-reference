package org.squidmin.java.spring.maven.batch.io;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.batch.domain.Order;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class OrderItemReader {

    @Bean
    public JdbcPagingItemReader<Order> orderReader(DataSource dataSource) {
        var reader = new JdbcPagingItemReader<Order>();
        reader.setDataSource(dataSource);
        reader.setPageSize(500);
        reader.setRowMapper(orderRowMapper());
        reader.setQueryProvider(queryProvider());
        reader.setParameterValues(Map.of("createdAfter", OffsetDateTime.now().minusDays(90)));
        return reader;
    }

    private PagingQueryProvider queryProvider() {
        var provider = new PostgresPagingQueryProvider();
        provider.setSelectClause("""
            SELECT id, external_order_id, email, currency, amount, created_at, cancelled
            """);
        provider.setFromClause("FROM raw_orders");
        provider.setWhereClause("WHERE created_at >= :createdAfter");
        provider.setSortKeys(Map.of("id", org.springframework.batch.item.database.Order.ASCENDING));
        return provider;
    }

    private RowMapper<Order> orderRowMapper() {
        return (ResultSet rs, int rowNum) -> new Order(
            rs.getLong("id"),
            rs.getString("external_order_id"),
            rs.getString("email"),
            rs.getString("currency"),
            rs.getBigDecimal("amount"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getBoolean("cancelled")
        );
    }

}
