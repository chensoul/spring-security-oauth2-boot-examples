package com.chensoul.oauth2.authorization.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security.oauth2", name = "client-type", havingValue = "memory")
public class InMemoryClientDetailsConfiguration {
	@Bean
	public ClientDetailsService clientDetailsService() {
		InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();
		Map<String, BaseClientDetails> baseClientDetailsMap = new HashMap<>();
		BaseClientDetails baseClientDetails = new BaseClientDetails("client", "", "server,profile", "authorization_code,password,refresh_token,client_credentials", null);
		baseClientDetails.setRegisteredRedirectUri(new HashSet<>(Arrays.asList("http://localhost:8082/", "http://localhost:8082/login")));
		baseClientDetails.setClientSecret("{noop}secret");
		baseClientDetailsMap.put("client", baseClientDetails);
		clientDetailsService.setClientDetailsStore(baseClientDetailsMap);
		return clientDetailsService;
	}

	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new InMemoryAuthorizationCodeServices();
	}

	@Bean
	public ApprovalStore approvalStore() {
		return new InMemoryApprovalStore();
	}
}
