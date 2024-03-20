package com.chensoul.oauth2.resource.configuration;

import com.chensoul.oauth2.common.security.RandomAuthenticationKeyGenerator;
import java.io.IOException;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 */
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2.resource.jwk", name = "key-set-uri", havingValue = "", matchIfMissing = true)
@Import({TokenConfiguration.JwkTokenConfiguration.class,
	TokenConfiguration.JdbcTokenConfiguration.class,
	TokenConfiguration.RedisTokenConfiguration.class
})
public class TokenConfiguration {

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwt")
	public class JwtTokenConfiguration {
		@Nullable
		private final AuthenticationManager authenticationManager;
		private final JwtAccessTokenConverter jwtTokenEnhancer;
		@Nullable
		private final UserDetailsService userDetailsService;

		@Bean
		public DefaultTokenServices jwtTokenServices(TokenStore jwtTokenStore) {
			DefaultTokenServices services = new DefaultTokenServices();
			services.setTokenStore(jwtTokenStore);
			if (authenticationManager != null) {
				services.setAuthenticationManager(authenticationManager);
			}
			return services;
		}

		@Bean
		public TokenStore jwtTokenStore() {
			if (userDetailsService != null) {
				DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter() {
					@Override
					public Authentication extractAuthentication(Map<String, ?> map) {
						Authentication authentication = super.extractAuthentication(map);
						if (authentication != null && authentication.getPrincipal() instanceof CredentialsContainer) {
							((CredentialsContainer) authentication.getPrincipal()).eraseCredentials();
						}
						return authentication;
					}
				};
				userAuthenticationConverter.setUserDetailsService(userDetailsService);

				DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
				accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);

				jwtTokenEnhancer.setAccessTokenConverter(accessTokenConverter);
			}

			return new JwtTokenStore(jwtTokenEnhancer);
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwk", matchIfMissing = true)
	public class JwkTokenConfiguration {
		private final ResourceServerProperties resource;
		@Nullable
		private final AuthenticationManager authenticationManager;

		@Bean
		public DefaultTokenServices jwkTokenServices(TokenStore jwkTokenStore) {
			DefaultTokenServices services = new DefaultTokenServices();
			services.setTokenStore(jwkTokenStore);
			if (authenticationManager != null) {
				services.setAuthenticationManager(authenticationManager);
			}
			return services;
		}

		@Bean
		public TokenStore jwkTokenStore() {
			return new JwkTokenStore(this.resource.getJwk().getKeySetUri());
		}
	}

	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "remote")
	@AllArgsConstructor
	public class RemoteTokenConfiguration {
		private final ResourceServerProperties resource;
		@Nullable
		private final UserDetailsService userDetailsService;

		private final RestTemplate restTemplate;

		@Bean
		@ConditionalOnMissingBean(ResourceServerTokenServices.class)
		public RemoteTokenServices remoteTokenServices() {
			RemoteTokenServices services = new RemoteTokenServices();
			services.setCheckTokenEndpointUrl(this.resource.getTokenInfoUri());
			services.setClientId(this.resource.getClientId());
			services.setClientSecret(this.resource.getClientSecret());

			restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
				@Override
				// Ignore 400
				public void handleError(ClientHttpResponse response) throws IOException {
					if (response.getRawStatusCode() != HttpStatus.BAD_REQUEST.value()) {
						super.handleError(response);
					}
				}
			});
			services.setRestTemplate(restTemplate);

			if (userDetailsService != null) {
				DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
				DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter() {
					@Override
					public Authentication extractAuthentication(Map<String, ?> map) {
						Authentication authentication = super.extractAuthentication(map);
						if (authentication != null && authentication.getPrincipal() instanceof CredentialsContainer) {
							((CredentialsContainer) authentication.getPrincipal()).eraseCredentials();
						}
						return authentication;
					}
				};
				userAuthenticationConverter.setUserDetailsService(userDetailsService);
				accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
				services.setAccessTokenConverter(accessTokenConverter);
			}
			return services;
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "redis")
	public class RedisTokenConfiguration {
		private RedisConnectionFactory redisConnectionFactory;

		@Bean
		public TokenStore tokenStore() {
			RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
			redisTokenStore.setPrefix("custom:");
			redisTokenStore.setAuthenticationKeyGenerator(new RandomAuthenticationKeyGenerator());
			return redisTokenStore;
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jdbc")
	public class JdbcTokenConfiguration {
		private Environment env;

		@Bean
		public DataSource dataSource() {
			final DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
			dataSource.setUrl(env.getProperty("jdbc.url"));
			dataSource.setUsername(env.getProperty("jdbc.username"));
			dataSource.setPassword(env.getProperty("jdbc.password"));
			return dataSource;
		}

		@Bean
		public TokenStore tokenStore() {
			JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource());
			return jdbcTokenStore;
		}
	}
}
