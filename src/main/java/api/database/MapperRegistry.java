package api.database;

import api.dao.UserDao;
import api.dao.AccountDao;

import java.util.HashMap;
import java.util.Map;

public class MapperRegistry {

    private static final Map<Class<?>, RowMapper<?>> mappers = new HashMap<>();

    static {
        mappers.put(UserDao.class, rs -> {
            if (rs.next()) {
                return UserDao.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .name(rs.getString("name"))
                        .build();
            }
            return null;
        });

        mappers.put(AccountDao.class, rs -> {
            if (rs.next()) {
                return AccountDao.builder()
                        .id(rs.getLong("id"))
                        .accountNumber(rs.getString("account_number"))
                        .balance(rs.getBigDecimal("balance"))
                        .customerId(rs.getLong("customer_id"))
                        .build();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> RowMapper<T> getMapper(Class<T> clazz) {
        RowMapper<?> mapper = mappers.get(clazz);

        if (mapper == null) {
            throw new UnsupportedOperationException(
                    "No mapper registered for " + clazz.getSimpleName()
            );
        }

        return (RowMapper<T>) mapper;
    }
}