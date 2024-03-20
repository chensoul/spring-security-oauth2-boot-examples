package com.chensoul.oauth2.authorization.configuration;

import com.chensoul.oauth2.common.security.JwkSetAccessTokenConverter;
import com.chensoul.oauth2.common.security.RandomAuthenticationKeyGenerator;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@AllArgsConstructor
public class TokenConfiguration {
	private AuthorizationServerProperties properties;

	@Configuration
	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwt")
	public class JwtTokenConfiguration {
		@Nullable
		private final AuthenticationManager authenticationManager;
		@Nullable
		private final UserDetailsService userDetailsService;
		private final ResourceServerProperties resource;

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
			JwtAccessTokenConverter jwtTokenEnhancer = jwtTokenEnhancer();
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

		@Bean
		public JwtAccessTokenConverter jwtTokenEnhancer() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setSigningKey(resource.getJwt().getKeyValue());
			return converter;
		}
	}

	/**
	 * jwk token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwk", matchIfMissing = true)
	public class JwkTokenConfiguration {
		private final String JWK_KID = RandomStringUtils.randomAlphanumeric(6);

		@Bean
		public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
			return new JwtTokenStore(jwtAccessTokenConverter);
		}

		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			Map<String, String> customHeaders = Collections.singletonMap("kid", JWK_KID);
			return new JwkSetAccessTokenConverter(customHeaders, keyPair());
		}

		public KeyPair keyPair() {
			ClassPathResource ksFile = new ClassPathResource(properties.getJwt().getKeyStore());
			KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, properties.getJwt().getKeyStorePassword().toCharArray());
			return ksFactory.getKeyPair(properties.getJwt().getKeyAlias(), properties.getJwt().getKeyPassword().toCharArray());
		}

		@Bean
		public JWKSet jwkSet() {
			RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic());
			builder.keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID(JWK_KID);
			return new JWKSet(builder.build());
		}

		@Order(-1)
		@Configuration
		@AutoConfigureAfter(JwkTokenConfiguration.class)
		public class JwkSetEndpointConfiguration extends AuthorizationServerSecurityConfiguration {
			/**
			 * @param http the {@link HttpSecurity} to modify for enabling the endpoint.
			 * @throws Exception
			 */
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http.authorizeRequests().antMatchers("/jwks").permitAll()
					.and().requestMatchers().antMatchers("/jwks");
				super.configure(http);
			}

			@RestController
			@AllArgsConstructor
			public class JwkSetEndpoint {
				private JWKSet jwkSet;

				@GetMapping("/jwks")
				public Map<String, Object> keys() {
					return this.jwkSet.toJSONObject();
				}
			}
		}
	}

	/**
	 * jdbc token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jdbc")
	public class JdbcTokenConfiguration {
		@Autowired
		private Environment env;

		@Value("classpath:schema.sql")
		private Resource schemaScript;

		@Value("classpath:data.sql")
		private Resource dataScript;

		/**
		 * @param dataSource
		 * @return
		 */
		@Bean
		public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
			final DataSourceInitializer initializer = new DataSourceInitializer();
			initializer.setDataSource(dataSource);
			initializer.setDatabasePopulator(databasePopulator());
			return initializer;
		}

		/**
		 * @return
		 */
		private DatabasePopulator databasePopulator() {
			final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.addScript(schemaScript);
			populator.addScript(dataScript);
			return populator;
		}

		/**
		 * @return data source
		 */
		@Bean
		public DataSource dataSource() {
			final DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
			dataSource.setUrl(env.getProperty("jdbc.url"));
			dataSource.setUsername(env.getProperty("jdbc.username"));
			dataSource.setPassword(env.getProperty("jdbc.password"));
			return dataSource;
		}

		/**
		 * @return token store
		 */
		@Bean
		public TokenStore tokenStore() {
			JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource());
			jdbcTokenStore.setAuthenticationKeyGenerator(new RandomAuthenticationKeyGenerator());
			return jdbcTokenStore;
		}

		/**
		 * @return approval store
		 */
		@Bean
		public ApprovalStore approvalStore() {
			return new JdbcApprovalStore(dataSource());
		}

		/**
		 * @return authorization code services
		 */
		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new JdbcAuthorizationCodeServices(dataSource());
		}
	}

	/**
	 * redis token 配置
	 */
	@AllArgsConstructor
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "redis")
	public class RedisTokenConfiguration {
		private RedisConnectionFactory redisConnectionFactory;

		/**
		 * @return token store
		 */
		@Bean
		public TokenStore tokenStore() {
			RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
			redisTokenStore.setPrefix("custom:");
			redisTokenStore.setAuthenticationKeyGenerator(new RandomAuthenticationKeyGenerator());
			return redisTokenStore;
		}
	}
}
