package com.chensoul.oauth2.common.security;

import com.chensoul.oauth2.common.security.util.LoggedUserDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * jdbc user details service
 */
public class SimpleJdbcUserDetailsService implements UserDetailsService {
	/**
	 * jdbc template
	 */
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param dataSource data source
	 */
	public SimpleJdbcUserDetailsService(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * @param username the username identifying the user whose data is required. Cannot be null.
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public LoggedUserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		String userSQLQuery = "SELECT * FROM USERS WHERE USERNAME=? limit 1";
		LoggedUserDetails loggedUserDetails = jdbcTemplate.queryForObject(userSQLQuery, new String[]{username}, new RowMapper<LoggedUserDetails>() {
			@Nullable
			@Override
			public LoggedUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoggedUserDetails user = new LoggedUserDetails();
				user.setId(rs.getLong("ID"));
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setPermissions(Arrays.asList("ADMIN"));
				return user;
			}
		});
		return loggedUserDetails;
	}
}
