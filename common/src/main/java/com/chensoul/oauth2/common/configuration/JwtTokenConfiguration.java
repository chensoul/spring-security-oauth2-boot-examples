package com.chensoul.oauth2.common.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwt")
public class JwtTokenConfiguration {
  public static final String DEFAULT_SECRET = "cocktail-cloud";

  private final ResourceServerProperties resource;

  @Nullable
  private final AuthenticationManager authenticationManager;
  @Nullable
  private final UserDetailsService userDetailsService;

  @Bean
  public DefaultTokenServices defaultTokenServices(TokenStore jwtTokenStore) {
    DefaultTokenServices tokenServices = new DefaultTokenServices();
    tokenServices.setTokenStore(jwtTokenStore);
    tokenServices.setSupportRefreshToken(true); //支持刷新 token
    tokenServices.setReuseRefreshToken(true);

    //刷新 token 时候需要验证用户
    if (authenticationManager != null) {
      tokenServices.setAuthenticationManager(authenticationManager);
    }
    return tokenServices;
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter jwtAccessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(StringUtils.defaultString(resource.getJwt().getKeyValue(), DEFAULT_SECRET));

    DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();

    //从 UserDetailsService 加载用户，如果是资源服务器，需要配置一个 userDetailsService
    if (userDetailsService != null) {
      userAuthenticationConverter.setUserDetailsService(userDetailsService);
    }

    DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
    accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
    converter.setAccessTokenConverter(accessTokenConverter);

    return converter;
  }
}
