package com.chensoul.oauth2.common.support;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public class RemoteUserAuthenticationConverter extends JwtUserAuthenticationConverter implements UserAuthenticationConverter {
	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey("data")) {
			Map<String, Object> data = (Map<String, Object>) map.get("data");
			return doEextractAuthentication(data);
		}
		return null;
	}
}
