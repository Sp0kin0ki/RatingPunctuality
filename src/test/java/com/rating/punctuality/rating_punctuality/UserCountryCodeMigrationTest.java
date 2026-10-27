package com.rating.punctuality.rating_punctuality;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserCountryCodeMigrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("punctuality_rating")
            .withUsername("postgres")
            .withPassword("postgres");

    @Test
    void shouldPreserveLegacyUserAndBackfillCountryCode() throws Exception {
        migrateToV4();

        UUID legacyUserId = UUID.randomUUID();
        insertLegacyUser(legacyUserId.toString());

        assertTrue(countryCodeIsNullable());
        assertNull(findCountryCode(legacyUserId.toString()));

        migrateToLatest();

        assertFalse(countryCodeIsNullable());
        assertEquals("ZZ", findCountryCode(legacyUserId.toString()));
        assertEquals(5, successfulBusinessMigrations());
    }

    private void migrateToV4() {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .target(MigrationVersion.fromVersion("4"))
                .load()
                .migrate();
    }

    private void migrateToLatest() {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    private void insertLegacyUser(String id) throws Exception {
        String sql = """
                INSERT INTO users (id, created_at, updated_at, username, password, email, description)
                VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?, ?, ?)
                """;

        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, "legacy_user");
            statement.setString(3, "legacy_password_hash");
            statement.setString(4, "legacy@example.com");
            statement.setString(5, "Legacy user created before V5");
            statement.executeUpdate();
        }
    }

    private String findCountryCode(String id) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT country_code FROM users WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getString(1);
            }
        }
    }

    private boolean countryCodeIsNullable() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT is_nullable
                     FROM information_schema.columns
                     WHERE table_schema = 'public'
                       AND table_name = 'users'
                       AND column_name = 'country_code'
                     """)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return "YES".equals(resultSet.getString(1));
            }
        }
    }

    private int successfulBusinessMigrations() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT COUNT(*)
                     FROM flyway_schema_history
                     WHERE success = TRUE
                       AND version IN ('1', '2', '3', '4', '5')
                     """)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getInt(1);
            }
        }
    }
}
