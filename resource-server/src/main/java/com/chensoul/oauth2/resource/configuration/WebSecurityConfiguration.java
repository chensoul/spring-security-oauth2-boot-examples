package com.chensoul.oauth2.resource.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(PermitUrlProperties.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private AccessDeniedHandler accessDeniedHandler;

	public WebSecurityConfiguration(AccessDeniedHandler accessDeniedHandler) {
		this.accessDeniedHandler = accessDeniedHandler;
	}

	protected void configure(HttpSecurity http) throws Exception {
		//前后端分离项目，不需要session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
			.and()
			.formLogin().disable()
			.authorizeRequests().anyRequest().authenticated()
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler);
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico", "/error", "/actuator");
	}
}
