package api.database.mapper;

import api.dao.UserDao;
import api.database.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<UserDao> {

    @Override
    public UserDao map(ResultSet rs) throws SQLException {
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
    }
}