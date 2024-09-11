package com.chensoul.oauth2.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ErrorConfiguration {

	@Bean
	@Order(-2)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public GlobalExceptionHandler reactiveExceptionHandler() {
		return new GlobalExceptionHandler();
	}
}
