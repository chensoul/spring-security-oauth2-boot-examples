package com.chensoul.oauth2.authorization.configuration;

import com.chensoul.oauth2.common.security.exception.CustomWebResponseExceptionTranslator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenKeyEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelErrorEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

/**
 * Authorization Server Configuration
 *
 * <p>
 *
 * <a href="http://127.0.0.1:8000/oauth/authorize?client_id=client&redirect_uri=http://127.0.0.1:123&response_type=code&scope=server&state=beff3dfc-bad8-40db-b25f-e5459e3d6ad7">授权码模式：获取授权码</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/token?code=oW9ca3&client_id=client&client_secret=secret&redirect_uri=http://127.0.0.1:123&grant_type=authorization_code">授权码模式：获取 Token</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/authorize?client_id=client&redirect_uri=http://127.0.0.1:123&response_type=token&scope=server">implicit模式</a><br>
 * <a href="http://127.0.0.1:8000/oauth/token?client_id=client&client_secret=secret&grant_type=refresh_token&refresh_token=">刷新 Token</a> <br/>
 * <a href="http://127.0.0.1:8000/oauth/token?grant_type=client_credentials&client_id=client&client_secret=secret&scope=server">client_credentials模式</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/check_token?token=">检查 token</a>
 * </p>
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @see DefaultLoginPageGeneratingFilter
 * @see OAuth2ClientAuthenticationProcessingFilter
 * @see ClientCredentialsTokenEndpointFilter
 * @see AuthorizationServerEndpointsConfiguration
 * @see AuthorizationServerSecurityConfiguration
 * @see WhitelabelApprovalEndpoint 。
 * @see WhitelabelErrorEndpoint 。
 * @see AuthorizationEndpoint
 * @see TokenEndpoint
 * @see TokenKeyEndpoint
 * @see CheckTokenEndpoint
 * @see JwtAccessTokenConverter
 * @see JwtTokenStore
 * @since 4.0.0
 */
@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final AuthorizationCodeServices authorizationCodeServices;
	private final ApprovalStore approvalStore;

	@Nullable
	private final AccessTokenConverter accessTokenConverter;
	@Nullable
	private final TokenEnhancer tokenEnhancer;

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()").allowFormAuthenticationForClients();
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			// Token 存储方式
			.tokenStore(tokenStore)
			// 密码模式需要验证用户
			.authenticationManager(authenticationManager)
			// 授权码模式需要
			.authorizationCodeServices(authorizationCodeServices)
			// 授权码模式
			.approvalStore(approvalStore)
			// 刷新 Token 时查询用户
			.userDetailsService(userDetailsService)
			// 校验 scope
			.requestValidator(new DefaultOAuth2RequestValidator())
			// 自定义OAuth2异常转换
			.exceptionTranslator(new CustomWebResponseExceptionTranslator());

		// Token 转化器，JWT 需要
		if (accessTokenConverter != null) {
			endpoints.accessTokenConverter(accessTokenConverter);
		}

		// Token 增强，JWT 需要
		if (tokenEnhancer != null) {
			final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer));
			endpoints.tokenEnhancer(tokenEnhancerChain);
		}
	}

}
