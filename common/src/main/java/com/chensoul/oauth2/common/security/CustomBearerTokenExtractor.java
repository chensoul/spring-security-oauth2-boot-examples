package com.chensoul.oauth2.common.security;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

/**
 * Custom BearerTokenExtractor，security request header token.
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @since 4.0.0
 */
public class CustomBearerTokenExtractor extends BearerTokenExtractor {
    /**
     * @param request The request.
     * @return
     */
    @Override
    protected String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(OAuth2AccessToken.ACCESS_TOKEN);
        while (headers.hasMoreElements()) {
            String token = headers.nextElement();
            if (token != null) {
                return token;
            }
        }
        return super.extractHeaderToken(request);
    }
}
