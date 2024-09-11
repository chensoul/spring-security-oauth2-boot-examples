package com.chensoul.oauth2.common.configuration;

import com.chensoul.oauth2.common.constants.SecurityConstants;
import com.chensoul.oauth2.common.support.CustomBearerTokenExtractor;
import com.chensoul.oauth2.common.support.CustomWebResponseExceptionTranslator;
import com.chensoul.oauth2.common.support.PermissionService;
import com.chensoul.oauth2.common.support.SimpleJdbcUserDetailsService;
import com.chensoul.oauth2.common.util.SecurityUtils;
import feign.RequestInterceptor;
import java.util.Collection;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.event.LoggerListener;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.CollectionUtils;

@Configuration
@ConditionalOnClass(Servlet.class)
public class OAuth2CommonConfiguration {
  /**
   * web响应异常转换器
   */
  @Bean
  public WebResponseExceptionTranslator webResponseExceptionTranslator() {
    return new CustomWebResponseExceptionTranslator();
  }

  /**
   * OAuth2 AccessDeniedHandler
   */
  @Bean
  public AccessDeniedHandler accessDeniedHandler(WebResponseExceptionTranslator exceptionTranslator) {
    OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
    accessDeniedHandler.setExceptionTranslator(exceptionTranslator);
    return accessDeniedHandler;
  }

  /**
   * OAuth2 AuthenticationEntryPoint
   */
  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(WebResponseExceptionTranslator exceptionTranslator) {
    OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
    authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
    return authenticationEntryPoint;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean("pms")
  public PermissionService permissionService() {
    return new PermissionService();
  }

  @Bean
  public LoggerListener loggerListener() {
    return new LoggerListener();
  }

  @Bean
  public BearerTokenExtractor bearerTokenExtractor() {
    return new CustomBearerTokenExtractor();
  }

  @Bean
  public org.springframework.security.authentication.event.LoggerListener oauthLoggerListener() {
    return new org.springframework.security.authentication.event.LoggerListener();
  }

  @Bean
  @ConditionalOnMissingBean
  public UserDetailsService userDetailsService(DataSource dataSource) {
    return new SimpleJdbcUserDetailsService(dataSource);
  }

  @Bean
  @ConditionalOnProperty("security.oauth2.client.client-id")
  public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ProtectedResourceDetails resource) {
    return requestTemplate -> {
      // 增加客户端标识
      requestTemplate.header(OAuth2Utils.CLIENT_ID, resource.getClientId());
      // 增加国际化的头
      requestTemplate.header(HttpHeaders.ACCEPT_LANGUAGE, LocaleContextHolder.getLocale().toLanguageTag());

      Collection<String> fromHeader = requestTemplate.headers().get(SecurityConstants.FROM);
      if (!CollectionUtils.isEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_IN)) {
        return;
      }
      String authorizationToken = SecurityUtils.getCurrentToken();
      requestTemplate.header(HttpHeaders.AUTHORIZATION, SecurityConstants.OAUTH2_TOKEN_TYPE + authorizationToken);
    };
  }
}
