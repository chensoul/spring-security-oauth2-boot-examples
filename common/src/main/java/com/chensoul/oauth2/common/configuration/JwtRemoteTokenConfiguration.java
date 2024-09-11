package com.chensoul.oauth2.common.configuration;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwt-remote")
public class JwtRemoteTokenConfiguration {
  private final ResourceServerProperties resource;
  @Nullable
  private final UserDetailsService userDetailsService;

  @Bean
  @ConditionalOnMissingBean(ResourceServerTokenServices.class)
  public RemoteTokenServices remoteTokenServices() {
    RemoteTokenServices services = new RemoteTokenServices();
    services.setCheckTokenEndpointUrl(this.resource.getTokenInfoUri());
    services.setClientId(this.resource.getClientId());
    services.setClientSecret(this.resource.getClientSecret());

    //使用 restful 获取用户信息
    services.setRestTemplate(lbRestTemplate());

    //从 UserDetailsService 加载用户，如果是资源服务器，需要配置一个 userDetailsService
    if (userDetailsService != null) {
      DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
      DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();
      userAuthenticationConverter.setUserDetailsService(userDetailsService);
      accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
      services.setAccessTokenConverter(accessTokenConverter);
    }
    return services;
  }

  @Bean
  @Primary
//    @LoadBalanced
  public RestTemplate lbRestTemplate() {
    // 如果 RestTemplate 处理了异常，过滤器（ CocktailAuthExceptionEntryPoint ）就不会处理异常
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getRawStatusCode() != HttpStatus.BAD_REQUEST.value() && response.getRawStatusCode() != HttpStatus.UNAUTHORIZED.value()) {
          super.handleError(response);
        }
      }
    });
    return restTemplate;
  }
}
