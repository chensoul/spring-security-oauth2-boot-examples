package com.chensoul.oauth2.common.util;


import com.chensoul.oauth2.common.constants.SecurityConstants;
import com.chensoul.oauth2.common.exception.BusinessException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类
 */
public class SecurityUtils {
	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static SecurityUser getCurrentUser() {
		SecurityUser securityUser = getSecurityUser();
		if (securityUser == null) {
			throw new BusinessException("您没有登陆，无权限访问");
		}
		return securityUser;
	}

	public static SecurityUser getSecurityUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
			return (SecurityUser) authentication.getPrincipal();
		}
		return null;
	}

	public static String getUsername() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return extractPrincipal(securityContext.getAuthentication());
	}

	private static String extractPrincipal(Authentication authentication) {
		if (authentication == null) {
			return null;
		}
		if (authentication.getPrincipal() instanceof UserDetails) {
			return ((UserDetails) authentication.getPrincipal()).getUsername();
		}
		if (authentication.getPrincipal() instanceof String) {
			return (String) authentication.getPrincipal();
		}
		return null;
	}

	public static Long getUserId() {
		return getCurrentUser().getId();
	}

	public static String getCurrentToken() {
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) getAuthentication().getDetails();
		return details.getTokenValue();
	}

	public static Set<String> getRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getAuthorities().stream().filter(granted -> StringUtils.startsWith(granted.getAuthority(), SecurityConstants.ROLE)).map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
	}

	public static String getClientId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
			return auth2Authentication.getOAuth2Request().getClientId();
		}

		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (servletRequestAttributes.getRequest() != null) {
				BasicAuthenticationConverter basicAuthenticationConverter = new BasicAuthenticationConverter();
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = basicAuthenticationConverter.convert(servletRequestAttributes.getRequest());
				if (usernamePasswordAuthenticationToken != null) {
					return usernamePasswordAuthenticationToken.getName();
				}
			}
		}

		//内部接口没有传递 token 时，header 中传递了客户端ID
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			return Objects.toString(requestAttributes.getRequest().getParameter("client_id"), requestAttributes.getRequest().getHeader("client_id"));
		}
		return null;
	}

}
