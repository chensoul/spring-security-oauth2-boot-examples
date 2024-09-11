package com.chensoul.oauth2.authorization.support;

import static com.chensoul.oauth2.common.constants.CacheConstants.LOGIN_FAIL_COUNT_CACHE;
import com.chensoul.oauth2.common.exception.ResultCode;
import com.chensoul.oauth2.common.util.ErrorResponse;
import com.chensoul.oauth2.common.util.JacksonUtils;
import com.chensoul.oauth2.common.util.ServletUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

@Slf4j
@AllArgsConstructor
public class AuthenticationLoginFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
	private static final long LIMIT_LOGIN_FAIL_COUNT = 5L;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		Long loginFailCount = 0L;
		String key = LOGIN_FAIL_COUNT_CACHE + event.getAuthentication().getName();
		Object object = redisTemplate.opsForValue().get(key);
		if (Objects.nonNull(object)) {
			loginFailCount = Long.parseLong(object.toString());
		}

		redisTemplate.opsForValue().set(key, loginFailCount + 1, 60L, TimeUnit.SECONDS);

		// 3分钟内连续5次失败则提示
		if (loginFailCount >= LIMIT_LOGIN_FAIL_COUNT) {
			redisTemplate.expire(key, 600L, TimeUnit.SECONDS);
			ServletUtils.renderJson(ServletUtils.getResponse().get(), HttpStatus.UNAUTHORIZED.value(), JacksonUtils.toJson(ErrorResponse.of(ResultCode.USER_LOCKED.getCode(), "因连续5次输错账号密码，该账户锁定10分钟")));
		}
	}
}
