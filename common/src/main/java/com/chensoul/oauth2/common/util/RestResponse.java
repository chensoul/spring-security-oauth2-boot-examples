/*
 * The MIT License
 *
 *  Copyright (c) 2021, wesine.com.cn
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.chensoul.oauth2.common.util;

import com.chensoul.oauth2.common.exception.ResultCode;
import com.chensoul.oauth2.common.exception.ResultCodeAware;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一封装 Restful 接口返回信息
 */
@NoArgsConstructor
@Data
public class RestResponse<T> implements java.io.Serializable {
//	private static MessageSource messageSource;

	static {
//		messageSource = SpringContextHolder.getBean(MessageSource.class);
	}

	private Integer code;
	private String message;
	private String error;
	private T data;

	private RestResponse(Integer code, String message, String error, T data) {
		this.code = code;
//		if (!Objects.equals(this.code, ResultCode.SUCCESS.getCode()) && Objects.nonNull(messageSource) && StringUtils.isNotBlank(message)) {
//			this.message = messageSource.getMessage(message, null, message, LocaleContextHolder.getLocale());
//		} else {
		this.message = message;
//		}
		this.error = error;
		this.data = data;
	}

	/**
	 * 一般的响应结果，返回异常信息和数据
	 *
	 * @param resultCodeAware {@link ResultCode} 自定义状态码
	 * @param data            数据对象
	 * @param errorDetail     异常信息
	 */
	private RestResponse(ResultCodeAware resultCodeAware, T data, String errorDetail) {
		this(resultCodeAware.getCode(), resultCodeAware.getMessage(), errorDetail, data);
	}

	/**
	 * 一般的响应结果，不返回数据和异常信息
	 *
	 * @param resultCodeAware {@link ResultCode} 自定义状态码
	 */
	private RestResponse(ResultCodeAware resultCodeAware) {
		this(resultCodeAware, null, null);
	}

	/**
	 * 请求成功
	 *
	 * @return this
	 */
	public static RestResponse ok() {
		return new RestResponse(ResultCode.SUCCESS);
	}

	/**
	 * 请求成功，包含数据部分
	 *
	 * @param data 数据对象
	 * @return this
	 */
	public static RestResponse ok(Object data) {
		return new RestResponse(ResultCode.SUCCESS, data, null);
	}

	/**
	 * 请求失败，自定义状态码
	 *
	 * @param resultCodeAware {@link ResultCode} 自定义状态码
	 * @return this
	 */
	public static RestResponse error(ResultCodeAware resultCodeAware) {
		return new RestResponse(resultCodeAware);
	}

	/**
	 * 请求失败，自定义状态码，并返回异常信息
	 *
	 * @param resultCodeAware {@link ResultCode} 自定义状态码
	 * @param errorDetail     异常信息
	 * @return this
	 */
	public static RestResponse error(ResultCodeAware resultCodeAware, String errorDetail) {
		return new RestResponse(resultCodeAware.getCode(), errorDetail, resultCodeAware.getMessage(), null);
	}

	/**
	 * 请求失败，这里不支持修改提示消息
	 *
	 * @param errorDetail 异常信息
	 * @return this
	 */
	public static RestResponse error(String errorDetail) {
		return new RestResponse(ResultCode.INTERNAL_ERROR.getCode(), errorDetail, errorDetail, null);
	}

	public static <T> RestResponse<T> error(ResultCodeAware resultCodeAware, Object error) {
		return new RestResponse(resultCodeAware.getCode(), resultCodeAware.getMessage(), null, error);
	}

	public static <T> RestResponse<T> error(ResultCodeAware resultCodeAware, String message, Object error) {
		return new RestResponse(resultCodeAware.getCode(), message, resultCodeAware.getMessage(), error);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("code", this.getCode());
		map.put("message", this.getMessage());
		map.put("data", this.getData());

		return map;
	}
}
