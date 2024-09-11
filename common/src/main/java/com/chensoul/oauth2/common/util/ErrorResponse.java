package com.chensoul.oauth2.common.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

/**
 * 异常响应实体
 */
@Getter
@ToString
public class ErrorResponse {
	private static final String TRACE_ID = "traceId";
	private final int code;
	private final String message;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;
	private String traceId;

	protected ErrorResponse(final int code, final String message, final String traceId) {
		this.code = code;
		this.message = message;
		this.traceId = traceId;
		this.timestamp = LocalDateTime.now();
	}

	protected ErrorResponse(final int code, final String message) {
		this(code, message, ObjectUtils.defaultIfNull(MDC.get(TRACE_ID), UUID.randomUUID().toString().replaceAll(StringPool.DASH, StringPool.EMPTY)));
	}

	public static ErrorResponse of(final int code, final String message) {
		return new ErrorResponse(code, message);
	}
}
