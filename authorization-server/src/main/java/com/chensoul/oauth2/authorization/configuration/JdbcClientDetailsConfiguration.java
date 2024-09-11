package com.chensoul.oauth2.authorization.configuration;

import com.chensoul.oauth2.common.constants.SecurityConstants;
import com.chensoul.oauth2.common.support.CacheableJdbcClientDetailsService;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security.oauth2", name = "client-type", havingValue = "jdbc")
public class JdbcClientDetailsConfiguration {

	@Bean
	public JdbcClientDetailsService jdbcClientDetailsService(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
		CacheableJdbcClientDetailsService clientDetailsService = new CacheableJdbcClientDetailsService(dataSource, redisTemplate);
		clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
		clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
		return clientDetailsService;
	}

	@Bean
	public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Bean
	public ApprovalStore approvalStore(DataSource dataSource) {
		return new JdbcApprovalStore(dataSource);
	}
}
