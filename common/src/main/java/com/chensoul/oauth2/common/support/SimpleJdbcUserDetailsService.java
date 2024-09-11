package com.chensoul.oauth2.common.support;

import com.chensoul.oauth2.common.util.Authority;
import com.chensoul.oauth2.common.util.SecurityUser;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public SecurityUser loadUserByUsername(final String username) throws UsernameNotFoundException {
		String userSQLQuery = "SELECT * FROM USERS WHERE USERNAME=? limit 1";
		SecurityUser loggedUserDetails = jdbcTemplate.queryForObject(userSQLQuery, new String[]{username}, new RowMapper<SecurityUser>() {
			@Nullable
			@Override
			public SecurityUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				SecurityUser user = new SecurityUser();
				user.setId(rs.getLong("ID"));
				user.setUsername(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEnabled(true);
				user.setAuthority(Authority.SYS_ADMIN);
				return user;
			}
		});
		return loggedUserDetails;
	}
}
