package com.chensoul.oauth2.authorization.configuration;

import com.chensoul.oauth2.common.security.SimpleJdbcUserDetailsService;
import java.util.Arrays;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * TODO
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since 1.0.0
 */
@AllArgsConstructor
@Configuration
public class UserDetailsConfiguration {
	/**
	 * 密码编码器
	 */
	private final PasswordEncoder passwordEncoder;

	/**
	 * TODO
	 *
	 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
	 * @since 1.0.0
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "user-type", havingValue = "memory", matchIfMissing = true)
	public class InMemoryUserDetails {
		/**
		 * 用户详细信息服务
		 *
		 * @return {@link UserDetailsService}
		 */
		@Bean
		public UserDetailsService userDetailsService() {
			return new InMemoryUserDetailsManager(
				new User("user", passwordEncoder.encode("password"),
					Arrays.asList(new SimpleGrantedAuthority("ROLE_" + "USER"))),
				new User("admin", passwordEncoder.encode("password"),
					Arrays.asList(new SimpleGrantedAuthority("ROLE_" + "ADMIN")))
			);
		}
	}

	/**
	 * TODO
	 *
	 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
	 * @since 1.0.0
	 */
	@AllArgsConstructor
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "user-type", havingValue = "jdbc")
	public class JdbcUserDetails {
		/**
		 * 数据源
		 */
		private final DataSource dataSource;

		/**
		 * 用户详细信息服务
		 *
		 * @return {@link UserDetailsService}
		 */
		@Bean
		public UserDetailsService userDetailsService() {
			return new SimpleJdbcUserDetailsService(dataSource);
		}

		//	@Bean
		//	public UserDetailsService userDetailsService() {
		//		JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
		//		jdbcDao.setDataSource(dataSource);
		//		return jdbcDao;
		//	}
	}
}
