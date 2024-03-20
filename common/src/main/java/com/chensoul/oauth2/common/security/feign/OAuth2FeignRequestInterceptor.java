package com.chensoul.oauth2.common.security.feign;

import com.chensoul.oauth2.common.constants.SecurityConstants;
import com.chensoul.oauth2.common.security.util.SecurityUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

/**
 * TODO Comment
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since TODO
 */
@Slf4j
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate template) {
		log.info("Setting feign headers for OAuth2 request");

		template.header(SecurityConstants.HEADER_CLIENT_ID, SecurityUtils.getClientId());
		template.header(SecurityConstants.HEADER_GATEWAY_TOKEN, new String(Base64Utils.encode(SecurityConstants.GATEWAY_TOKEN_VALUE.getBytes())));

		// 内部请求不用设置 token
		Collection<String> fromHeader = template.headers().get(SecurityConstants.FROM);
		if (!CollectionUtils.isEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_IN)) {
			log.debug("The request is from inner, don't need to set oauth2 token");
			return;
		}

//		String authorizationToken = SecurityUtils.get();
//		template.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + " " + authorizationToken);
	}
}
