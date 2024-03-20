package com.chensoul.oauth2.authorization.controller;

import com.chensoul.oauth2.common.model.Result;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
	@Nullable
	private RedisTokenStore redisTokenStore;

	private UserDetailsService userDetailsService;

	@GetMapping("/info")
	public Result<UserDetails> info(final Authentication authentication) {
		final Object principal = authentication.getPrincipal();

		UserDetails userDetails = null;
		if (principal instanceof UserDetails) {
			userDetails = (UserDetails) principal;
		} else {
			final String username = principal.toString();
			userDetails = this.userDetailsService.loadUserByUsername(username);
			((CredentialsContainer) userDetails).eraseCredentials();
		}
		return Result.ok(userDetails);
	}

	/**
	 * 安全退出
	 *
	 * @param access_token
	 * @param authorization
	 * @return
	 */
	@GetMapping("/logout")
	public Result logout(String access_token, final String authorization) {
		if (StringUtils.isBlank(access_token)) {
			access_token = authorization;
		}
		if (StringUtils.isBlank(access_token)) {
			return Result.ok();
		}
		if (access_token.toLowerCase().contains("bearer ".toLowerCase())) {
			access_token = access_token.toLowerCase().replace("bearer ", "");
		}
		final OAuth2AccessToken oAuth2AccessToken = this.redisTokenStore.readAccessToken(access_token);
		if (oAuth2AccessToken != null && this.redisTokenStore != null) {
			this.redisTokenStore.removeAccessToken(oAuth2AccessToken);
			final OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
			this.redisTokenStore.removeRefreshToken(refreshToken);
		}
		return Result.ok();
	}

}
