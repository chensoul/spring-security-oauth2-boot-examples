package com.chensoul.oauth2.common.model;

import com.chensoul.oauth2.common.exception.BusinessException;
import com.chensoul.oauth2.common.webmvc.SpringContextHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 统一封装 Restful 接口返回信息
 *
 * @since 1.0.0
 */
@NoArgsConstructor
@Data
@Slf4j
public class Result<T> implements java.io.Serializable {

	@Getter
	private int code;

	private String message;

	private T data;

	private Result(int code, String message, T data) {
		this.code = code;
		this.data = data;

		if (StringUtils.isNotBlank(message)) {
			Locale locale = ObjectUtils.defaultIfNull(LocaleContextHolder.getLocale(), Locale.CHINA);
			this.message = SpringContextHolder.getMessage(message, null, message, locale);
		}
	}

	public static Result ok() {
		return ok(null);
	}

	public static Result ok(Object data) {
		return of(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getName(), data);
	}

	public static <T> Result<T> error(String message) {
		return error(ResultCode.SERVICE_ERROR, message);
	}

	public static <T> Result<T> error(ResultCode resultCode) {
		return error(resultCode, resultCode.getName());
	}

	public static <T> Result<T> error(ResultCode resultCode, String message) {
		return error(resultCode.getCode(), message);
	}

	public static <T> Result<T> error(int code, String message) {
		return of(code, message, null);
	}

	private static <T> Result<T> of(int code, String message, T data) {
		return new Result(code, message, data);
	}

	public Optional<T> data() {
		return data == null ? Optional.empty() : Optional.of(data);
	}

	public Optional<T> checkedData() {
		if (code != ResultCode.SUCCESS.getCode()) {
			//此处统一抛出的是业务异常，没有区分具体的异常类型
			throw new BusinessException(code, getMessage());
		}
		return Optional.of(getData());
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("code", this.getCode());
		map.put("message", this.getMessage());
		map.put("data", this.getData());

		return map;
	}
}
