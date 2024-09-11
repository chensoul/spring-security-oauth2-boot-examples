package com.chensoul.oauth2.common.util;

import com.chensoul.oauth2.common.exception.BusinessException;
import static com.chensoul.oauth2.common.util.StringPool.EMPTY;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This is {@link ServletUtils}.
 */
@UtilityClass
@Slf4j
public class ServletUtils {
	private final String BASIC_ = "Basic ";

	private static final List<String> CLIENT_IP_HEADER_NAMES = Arrays.asList("X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");

	private static final int PING_URL_TIMEOUT = 5_000;
	private static final String STAR_THREE = "***";
	private static final String HEADER_USER_AGENT = "user-agent";

	public static Optional<HttpServletRequest> getHttpServletRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return Optional.ofNullable(requestAttributes).map(ServletRequestAttributes::getRequest);
	}

	/**
	 * Gets http servlet response from request attributes.
	 *
	 * @return the http servlet response from request attributes
	 */
	public static Optional<HttpServletResponse> getResponse() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return Optional.ofNullable(requestAttributes).map(ServletRequestAttributes::getResponse);
	}

	/**
	 * Gets request headers.
	 *
	 * @param request the request
	 * @return the request headers
	 */
	@SuppressWarnings("JdkObsolete")
	public static Map<String, String> getRequestHeaders(final HttpServletRequest request) {
		Map<String, Object> headers = new LinkedHashMap();
		if (request != null) {
			val headerNames = request.getHeaderNames();
			if (headerNames != null) {
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					Object headerValue = StringUtils.stripToEmpty(request.getHeader(headerName));
					headers.put(headerName, headerValue);
				}
			}
		}
		return (Map) headers;
	}

	@SneakyThrows
	public String[] getClientId(String header) {
		if (header == null || !header.startsWith(BASIC_)) {
			throw new BusinessException("请求头中client信息为空");
		}
		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, StandardCharsets.UTF_8);

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BusinessException("Invalid basic authentication token");
		}
		return new String[]{token.substring(0, delim), token.substring(delim + 1)};
	}

	public String[] getClientId(ServerHttpRequest request) {
		String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		return getClientId(header);
	}

	/**
	 * 从request 获取CLIENT_ID
	 *
	 * @param request HttpServletRequest
	 * @return 数组
	 */
	@SneakyThrows
	public String[] getClientId(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		return getClientId(header);
	}

	public void renderJson(HttpServletResponse response, int httpStatus, Object result) {
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(httpStatus);
		try (PrintWriter out = response.getWriter()) {
			out.append(JacksonUtils.toJson(result));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Gets http servlet request user agent.
	 *
	 * @param request the request
	 * @return the http servlet request user agent
	 */
	public static String getUserAgent(final HttpServletRequest request) {
		if (request != null) {
			return request.getHeader(HEADER_USER_AGENT);
		}
		return null;
	}

	/**
	 * Check if a parameter exists.
	 *
	 * @param request the HTTP request
	 * @param name    the parameter name
	 * @return whether the parameter exists
	 */
	public static boolean doesParameterExist(final HttpServletRequest request, final String name) {
		val parameter = request.getParameter(name);
		if (StringUtils.isBlank(parameter)) {
			log.error("Missing request parameter: [{}]", name);
			return false;
		}
		log.debug("Found provided request parameter [{}]", name);
		return true;
	}

	/**
	 * Ping url and return http status.
	 *
	 * @param location the location
	 * @return the http status
	 */
	public static HttpStatus pingUrl(final String location) {
		try {
			URL url = new URI(location).toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(PING_URL_TIMEOUT);
			connection.setReadTimeout(PING_URL_TIMEOUT);
			connection.setRequestMethod(HttpMethod.HEAD.name());
			return HttpStatus.valueOf(connection.getResponseCode());
		} catch (final Exception e) {
			log.error("Could not ping URL [{}]: [{}]", location, e.getMessage());
		}
		return HttpStatus.SERVICE_UNAVAILABLE;

	}

	/**
	 * Gets full request url.
	 *
	 * @param request the request
	 * @return the full request url
	 */
	public static String getFullRequestUrl(final HttpServletRequest request) {
		return request.getRequestURL() + (request.getQueryString() != null ? '?' + request.getQueryString() : EMPTY);
	}

	public static String getValueFromRequest(String headerName) {
		return getValueFromRequest(getHttpServletRequest().get(), headerName);
	}

	/**
	 * @param request
	 * @param headerName
	 * @return
	 */
	public static String getValueFromRequest(HttpServletRequest request, String headerName) {
		if (request == null) {
			return null;
		}
		String value = request.getParameter(headerName);
		if (StringUtils.isNotBlank(value)) {
			return value;
		} else {
			value = request.getHeader(headerName);
		}
		return value;
	}

	public static boolean isJsonRequest(ServletRequest request) {
		return StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}

	public static String constructBaseUrl(HttpServletRequest request) {
		return String.format("%s://%s:%d", getScheme(request), getDomainName(request), getPort(request));
	}

	public static String getScheme(HttpServletRequest request) {
		String scheme = request.getScheme();
		String forwardedProto = request.getHeader("x-forwarded-proto");
		if (forwardedProto != null) {
			scheme = forwardedProto;
		}
		return scheme;
	}

	public static String getDomainName(HttpServletRequest request) {
		return request.getServerName();
	}

	public static String getDomainNameAndPort(HttpServletRequest request) {
		String domainName = getDomainName(request);
		String scheme = getScheme(request);
		int port = getPort(request);
		if (needsPort(scheme, port)) {
			domainName += ":" + port;
		}
		return domainName;
	}

	private static boolean needsPort(String scheme, int port) {
		boolean isHttpDefault = "http".equals(scheme.toLowerCase()) && port == 80;
		boolean isHttpsDefault = "https".equals(scheme.toLowerCase()) && port == 443;
		return !isHttpDefault && !isHttpsDefault;
	}

	public static int getPort(HttpServletRequest request) {
		String forwardedProto = request.getHeader("x-forwarded-proto");

		int serverPort = request.getServerPort();
		if (request.getHeader("x-forwarded-port") != null) {
			try {
				serverPort = request.getIntHeader("x-forwarded-port");
			} catch (NumberFormatException e) {
			}
		} else if (forwardedProto != null) {
			switch (forwardedProto) {
				case "http":
					serverPort = 80;
					break;
				case "https":
					serverPort = 443;
					break;
			}
		}
		return serverPort;
	}

}
