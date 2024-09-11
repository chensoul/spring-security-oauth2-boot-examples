package com.chensoul.oauth2.common.configuration;

import com.chensoul.oauth2.common.support.RandomAuthenticationKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "redis")
public class RedisTokenConfiguration {
	@Bean
	public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
		RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
		redisTokenStore.setPrefix("custom:");
		redisTokenStore.setAuthenticationKeyGenerator(new RandomAuthenticationKeyGenerator());
		return redisTokenStore;
	}
}
