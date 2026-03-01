package api.database.mapper;

import api.dao.AccountDao;
import api.database.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccountRowMapper implements RowMapper<AccountDao> {

    @Override
    public AccountDao map(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return AccountDao.builder()
                    .id(rs.getLong("id"))
                    .accountNumber(rs.getString("account_number"))
                    .balance(rs.getBigDecimal("balance"))
                    .customerId(rs.getLong("customer_id"))
                    .build();
        }
        return null;
    }
}