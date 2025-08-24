package co.com.crediya.r2dbc.config;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MySQLConnectionPool {
    public static final int INITIAL_SIZE = 5;
    public static final int MAX_SIZE = 10;
    public static final int MAX_IDLE_TIME = 30;
    public static final String VALIDATION_QUERY = "SELECT 1";

    @Bean
    public ConnectionFactory connectionFactory(MySQLConnectionProperties properties) {
        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
                .host(properties.host())
                .port(properties.port())
                .database(properties.database())
                .user(properties.username())
                .password(properties.password())
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(MySqlConnectionFactory.from(configuration))
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
                .validationQuery(VALIDATION_QUERY)
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}