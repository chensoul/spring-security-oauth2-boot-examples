package com.chensoul.oauth2.common.support;

import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_NICKNAME;
import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_USER_ID;
import com.chensoul.oauth2.common.util.SecurityUser;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * 自定义的 Jwt 增强器，往 jwt 中写入用户信息，缺点是暴露了过多用户信息
 * <p>
 * {@link JwtUserAuthenticationConverter}
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public class JwtTokenEnhancer implements TokenEnhancer {
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
		SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
		Map<String, Object> info = new HashMap<>();
		info.put(JWT_USER_ID, securityUser.getId());
		info.put(JWT_NICKNAME, securityUser.getUsername());
		((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
		return oAuth2AccessToken;
	}
}
