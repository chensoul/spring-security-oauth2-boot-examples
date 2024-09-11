package com.chensoul.oauth2.common.support;

import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

/**
 *
 */
public class RandomAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator {
	/**
	 * @param values
	 * @return
	 */
	@Override
	protected String generateKey(Map<String, String> values) {
		return super.generateKey(values) + RandomStringUtils.randomAlphabetic(8);
	}
}
