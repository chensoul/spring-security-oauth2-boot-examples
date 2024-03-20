package com.chensoul.oauth2.common.security.exception;

import com.chensoul.oauth2.common.model.Result;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class CustomOAuth2ExceptionJackson2Serializer extends StdSerializer<CustomOAuth2Exception> {

	/**
	 *
	 */
	protected CustomOAuth2ExceptionJackson2Serializer() {
		super(CustomOAuth2Exception.class);
	}

	@Override
	public void serialize(CustomOAuth2Exception e, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
		Result<String> errorResponse = Result.error(e.getMessage());
		log.error("{}", errorResponse);

		jgen.writeObject(errorResponse);
	}
}
