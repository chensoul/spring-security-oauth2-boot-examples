package com.chensoul.oauth2.authorization.support;

import static com.chensoul.oauth2.common.constants.CacheConstants.LOGIN_FAIL_COUNT_CACHE;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

@Slf4j
@AllArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
		Authentication authentication = authenticationSuccessEvent.getAuthentication();
		redisTemplate.delete(LOGIN_FAIL_COUNT_CACHE + authentication.getName());
	}
}
