package com.chensoul.oauth2.common.configuration;

import com.chensoul.oauth2.common.support.JwkKeyPairAccessTokenConverter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwk")
public class JwkTokenConfiguration {
	private static final String JWK_KID = RandomStringUtils.randomAlphanumeric(6);
	private final AuthorizationServerProperties properties;

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
		return new JwtTokenStore(jwtAccessTokenConverter);
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		Map<String, String> customHeaders = Collections.singletonMap("kid", JWK_KID);
		return new JwkKeyPairAccessTokenConverter(customHeaders, keyPair());
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
			http.authorizeRequests().antMatchers("/jwks").permitAll().and().requestMatchers().antMatchers("/jwks");
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
