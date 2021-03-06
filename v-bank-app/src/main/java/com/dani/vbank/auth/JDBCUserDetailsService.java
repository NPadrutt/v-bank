package com.dani.vbank.auth;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Log
@Component
public class JDBCUserDetailsService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT USERNAME, PASSWORD " +
                    "FROM USER U WHERE U.USERNAME = '" + uname + "'";
            log.warning(sql);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                final String username = resultSet.getString("USERNAME");
                final String password = resultSet.getString("PASSWORD");
                return User.withUsername(username).password(password).roles("USER").build();
            } else {
                throw new UsernameNotFoundException("No user found with name : " + uname);
            }
        }
    }
}
