package com.chensoul.oauth2.authorization.configuration;

import com.chensoul.oauth2.common.security.CacheableJdbcClientDetailsService;
import com.chensoul.oauth2.common.security.ExpiredRedisAuthorizationCodeServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;

/**
 * Client details configuration
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since 1.0.0
 */
@Configuration
public class ClientDetailsConfiguration {
	@Configuration
	@AllArgsConstructor
	@AutoConfigureBefore(ClientDetailsServiceConfiguration.class)
	@ConditionalOnProperty(prefix = "security.oauth2", name = "client-type", havingValue = "memory", matchIfMissing = true)
	public static class InMemoryClientDetailsConfig {

		@Bean
		public ClientDetailsService clientDetailsService() {
			InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();
			Map<String, BaseClientDetails> baseClientDetailsMap = new HashMap<>();
			BaseClientDetails baseClientDetails = new BaseClientDetails("client", "",
				"server,profile", "authorization_code,password,refresh_token,client_credentials",
				null);
//			baseClientDetails.setAutoApproveScopes(new HashSet<>(Arrays.asList("server")));
			baseClientDetails.setRegisteredRedirectUri(new HashSet<>(Arrays.asList("http://localhost:8082/", "http://localhost:8082/login")));
			baseClientDetails.setClientSecret("{noop}secret");
			baseClientDetailsMap.put("client", baseClientDetails);
			clientDetailsService.setClientDetailsStore(baseClientDetailsMap);
			return clientDetailsService;
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new InMemoryAuthorizationCodeServices();
		}

		@Bean
		public ApprovalStore approvalStore(DataSource dataSource) {
			return new JdbcApprovalStore(dataSource);
		}
	}

	@Configuration
	@AutoConfigureBefore(ClientDetailsServiceConfiguration.class)
	@ConditionalOnProperty(prefix = "security.oauth2", name = "client-type", havingValue = "jdbc")
	public static class JdbcClientDetailsConfig {

		/**
		 * client details service
		 *
		 * @return
		 */
		@Bean
		public ClientDetailsService clientDetailsService(DataSource dataSource, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
			return redisTemplate != null ?
				new CacheableJdbcClientDetailsService(dataSource, redisTemplate, objectMapper) :
				new JdbcClientDetailsService(dataSource);
		}

		/**
		 * authorization code services
		 *
		 * @return
		 */
		@Bean
		public AuthorizationCodeServices authorizationCodeServices(RedisConnectionFactory connectionFactory) {
			return new ExpiredRedisAuthorizationCodeServices(connectionFactory);
		}

		@Bean
		public ApprovalStore approvalStore(DataSource dataSource) {
			return new JdbcApprovalStore(dataSource);
		}
	}
}
