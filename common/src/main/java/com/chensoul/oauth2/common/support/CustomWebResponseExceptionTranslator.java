package com.chensoul.oauth2.common.support;

import com.chensoul.oauth2.common.exception.ResultCode;
import com.chensoul.oauth2.common.util.ErrorResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

/**
 * 包装 OAuth2Exception
 */
@Slf4j
public class CustomWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {
	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
		ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
		OAuth2Exception originEx = responseEntity.getBody();
		HttpHeaders headers = responseEntity.getHeaders();

		CustomOAuth2Exception customOAuth2Exception = CustomOAuth2Exception.from(originEx);
		return new ResponseEntity<>(customOAuth2Exception, headers, responseEntity.getStatusCode());
	}

	/**
	 * 包装 {@link OAuth2Exception}，使用 {@link CustomOAuth2ExceptionJackson2Serializer} 处理异常返回信息
	 */
	@JsonSerialize(using = CustomOAuth2ExceptionJackson2Serializer.class)
	public static class CustomOAuth2Exception extends OAuth2Exception {
		CustomOAuth2Exception(String msg) {
			super(msg);
		}

		public static CustomOAuth2Exception from(OAuth2Exception origin) {
			CustomOAuth2Exception ex = new CustomOAuth2Exception(origin.getMessage()) {
				@Override
				public String getOAuth2ErrorCode() {
					return origin.getOAuth2ErrorCode();
				}

				@Override
				public int getHttpErrorCode() {
					return origin.getHttpErrorCode();
				}

				@Override
				public Map<String, String> getAdditionalInformation() {
					return origin.getAdditionalInformation();
				}

				@Override
				public String toString() {
					return this.getSummary();
				}

				@Override
				public String getSummary() {
					return origin.getSummary();
				}
			};
			ex.initCause(origin);
			return ex;
		}
	}

	@Slf4j
	public static class CustomOAuth2ExceptionJackson2Serializer extends StdSerializer<CustomOAuth2Exception> {
		protected CustomOAuth2ExceptionJackson2Serializer() {
			super(CustomOAuth2Exception.class);
		}

		@Override
		public void serialize(CustomOAuth2Exception e, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
			ErrorResponse errorResponse = ErrorResponse.of(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
			log.error("{}", errorResponse);
			jgen.writeObject(errorResponse);
		}
	}
}
