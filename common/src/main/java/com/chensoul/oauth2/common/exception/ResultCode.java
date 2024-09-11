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
package com.chensoul.oauth2.common.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 只定义常见的、需要显示给用户的信息描述信息
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements ResultCodeAware {
	/**
	 * 成功状态码
	 **/
	SUCCESS(0, "SUCCESS"),

	/**
	 * 参数错误
	 **/
	BAD_REQUEST(1001, "PARAM_INVALID"),
	DATA_DUPLICATE(1010, "DATA_DUPLICATE"),
	NOT_FOUND(1011, "DATA_NOT_FOUND"),
	TOO_MANY_REQUESTS(1012, "TOO_MANY_REQUESTS"),

	/**
	 * 权限相关
	 **/
	FORBIDDEN(4000, "AUTHORIZED_FAIL"),
	UNAUTHORIZED(4001, "UNAUTHORIZED"),
	INVALID_CLIENT(4003, "INVALID_CLIENT"),
	INVALID_GRANT(4004, "INVALID_GRANT"),
	INVALID_TOKEN(4005, "INVALID_TOKEN"),

	USER_LOCKED(4010, "USER_LOCKED"),
	USER_LOGIN_ERROR(4011, "USER_LOGIN_ERROR"),
	USER_NOT_LOGIN(4012, "USER_NOT_LOGIN"),
	USER_PASSWORD_DECODE_FAIL(4013, "USER_PASSWORD_DECODE_FAIL"),
	USER_NOT_FOUND(4014, "USER_NOT_FOUND"),

	CAPTCHA_EXPIRED(4020, "CAPTCHA_EXPIRED"),
	CAPTCHA_WRONG(4021, "CAPTCHA_WRONG"),
	CAPTCHA_MISSING_PARAM_CODE(4022, "CAPTCHA_MISSING_PARAM_CODE"),
	CAPTCHA_MISSING_PARAM_KEY(4023, "CAPTCHA_MISSING_PARAM_KEY"),
	CAPTCHA_MISSING_PARAM_SMS(4024, "CAPTCHA_MISSING_PARAM_SMS"),


	INTERNAL_ERROR(5000, "INTERNAL_ERROR"),
	INTERFACE_INNER_INVOKE_ERROR(5001, "INTERFACE_INNER_INVOKE_ERROR"),
	INTERFACE_OUTER_INVOKE_ERROR(5002, "INTERFACE_OUTER_INVOKE_ERROR"),
	INTERFACE_REQUEST_TIMEOUT(5003, "INTERFACE_REQUEST_TIMEOUT"),
	INTERFACE_EXCEED_LOAD(5004, "INTERFACE_EXCEED_LOAD");

	private static Map<Integer, ResultCode> reverseLookup = Arrays.asList(ResultCode.class.getEnumConstants()).stream()
		.collect(Collectors.toMap(ResultCode::getCode, t -> t));
	int code;
	String message;

	public static ResultCode fromCode(final Integer code) {
		return reverseLookup.getOrDefault(code, SUCCESS);
	}
}
