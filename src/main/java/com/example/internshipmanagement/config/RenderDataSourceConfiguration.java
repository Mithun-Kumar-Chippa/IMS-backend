package com.example.internshipmanagement.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("render")
public class RenderDataSourceConfiguration {

    @Bean
    public DataSource dataSource(@Value("${DATABASE_URL}") String databaseUrl) {
        URI databaseUri;
        try {
            databaseUri = URI.create(databaseUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("DATABASE_URL must be a valid PostgreSQL connection URL.");
        }

        String scheme = databaseUri.getScheme();
        String rawUserInfo = databaseUri.getRawUserInfo();
        String rawPath = databaseUri.getRawPath();
        int port = databaseUri.getPort() == -1 ? 5432 : databaseUri.getPort();
        if (!("postgres".equals(scheme) || "postgresql".equals(scheme))) {
            throw new IllegalStateException("DATABASE_URL must use the postgres:// or postgresql:// scheme.");
        }
        if (databaseUri.getHost() == null) {
            throw new IllegalStateException("DATABASE_URL is missing the PostgreSQL host.");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalStateException("DATABASE_URL contains an invalid PostgreSQL port.");
        }
        if (rawUserInfo == null || !rawUserInfo.contains(":")) {
            throw new IllegalStateException("DATABASE_URL must include both PostgreSQL username and password.");
        }
        if (rawPath == null || rawPath.length() < 2) {
            throw new IllegalStateException("DATABASE_URL is missing the PostgreSQL database name.");
        }

        String[] credentials = rawUserInfo.split(":", 2);
        String host = databaseUri.getHost();
        if (host.contains(":")) {
            host = "[" + host + "]";
        }

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + rawPath;
        if (databaseUri.getRawQuery() != null) {
            jdbcUrl += "?" + databaseUri.getRawQuery();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(decodeUrlComponent(credentials[0]));
        config.setPassword(decodeUrlComponent(credentials[1]));
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }

    private String decodeUrlComponent(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8);
    }
}