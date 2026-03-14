package api.database;

import api.senior.configs.Config;
import lombok.Builder;
import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DBRequest {

    private RequestType requestType;
    private String table;
    private List<Condition> conditions;

    public enum RequestType {
        SELECT, INSERT, UPDATE, DELETE
    }

    // 🔹 Главное изменение — принимаем RowMapper
    public <T> T extractAs(RowMapper<T> mapper) {
        return executeQuery(mapper);
    }

    private <T> T executeQuery(RowMapper<T> mapper) {

        String sql = buildSQL();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet rs = statement.executeQuery()) {
                return mapper.map(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    private String buildSQL() {

        StringBuilder sql = new StringBuilder();

        if (requestType == RequestType.SELECT) {
            sql.append("SELECT * FROM ").append(table);

            if (conditions != null && !conditions.isEmpty()) {
                sql.append(" WHERE ");

                for (int i = 0; i < conditions.size(); i++) {
                    if (i > 0) sql.append(" AND ");

                    sql.append(conditions.get(i).getColumn())
                            .append(" ")
                            .append(conditions.get(i).getOperator())
                            .append(" ?");
                }
            }
        } else {
            throw new UnsupportedOperationException(
                    "Request type " + requestType + " not implemented"
            );
        }

        return sql.toString();
    }

    public int executeUpdate(String sql, List<Object> params) {

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            return statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperty("db.url"),
                Config.getProperty("db.username"),
                Config.getProperty("db.password")
        );
    }

    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }

    public static class DBRequestBuilder {

        private RequestType requestType;
        private String table;
        private List<Condition> conditions = new ArrayList<>();

        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public <T> T extractAs(RowMapper<T> mapper) {
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .build();

            return request.extractAs(mapper);
        }
    }
}