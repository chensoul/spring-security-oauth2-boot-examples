package com.chensoul.oauth2.authorization.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableResourceServer
@AllArgsConstructor
@Order(3)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	private AuthenticationEntryPoint authenticationEntryPoint;
	private AccessDeniedHandler accessDeniedHandler;

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/user/**").permitAll();
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
		resources.authenticationEntryPoint(this.authenticationEntryPoint)
			.accessDeniedHandler(this.accessDeniedHandler);
	}

}
