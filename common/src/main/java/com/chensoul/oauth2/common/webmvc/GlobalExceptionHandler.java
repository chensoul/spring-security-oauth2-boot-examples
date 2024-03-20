package com.chensoul.oauth2.common.webmvc;

import com.chensoul.oauth2.common.exception.BusinessException;
import com.chensoul.oauth2.common.exception.SystemException;
import com.chensoul.oauth2.common.model.ResultCode;
import com.chensoul.oauth2.common.model.Result;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {
	/**
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = {BindException.class})
	@ResponseStatus(HttpStatus.OK)
	public static Result handleBindException(final HttpServletRequest request, final BindException e) {
		log.error("{}, 参数不合法", request.getRequestURI());
		return Result.error(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
	}

	/**
	 * @param request
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({BusinessException.class})
	public static Result handleBusinessException(final HttpServletRequest request, final BusinessException e) {
		log.error("{}, 业务异常", request.getRequestURI(), e);

		return Result.error(e.getCode(), e.getMessage());
	}

	/**
	 * @param request
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({Exception.class, SystemException.class})
	public static Result handleException(final HttpServletRequest request, final Exception e) {
		log.error("{}, 系统异常", request.getRequestURI(), e);
		return Result.error(ResultCode.SYSTEM_ERROR);
	}

}
