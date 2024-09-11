package com.chensoul.oauth2.common.configuration;

import com.chensoul.oauth2.common.exception.BusinessException;
import com.chensoul.oauth2.common.exception.ResultCode;
import static com.chensoul.oauth2.common.exception.ResultCode.INTERNAL_ERROR;
import com.chensoul.oauth2.common.util.ErrorResponse;
import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global Exception Handler
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	public GlobalExceptionHandler() {
		log.info("Initializing GlobalExceptionHandler");
	}

	/**
	 * 处理 SpringMVC 请求参数缺失
	 * <p>
	 * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
	 */
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数缺失: %s", ex.getParameterName()));
	}

	/**
	 * 处理 SpringMVC 请求参数类型错误
	 * <p>
	 * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数类型错误: %s", ex.getValue()));
	}

	/**
	 * 处理 SpringMVC 参数校验不正确
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		String message = null;
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			if (error instanceof FieldError) {
				FieldError field = (FieldError) error;
				message = field.getDefaultMessage();
				break;
			}
		}
		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", message));
	}

	/**
	 * 处理 SpringMVC 请求方法不正确
	 * <p>
	 * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("%s请求方法不支持", ex.getMethod()));
	}

	/**
	 * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
	 */
	@ExceptionHandler(BindException.class)
	public ErrorResponse handleBindException(final BindException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		String message = null;
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			if (error instanceof FieldError) {
				FieldError field = (FieldError) error;
				message = field.getDefaultMessage();
				break;
			}
		}

		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", message));
	}

	/**
	 * 处理 @Validated 校验不通过产生的异常
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorResponse handleConstraintViolationException(final ConstraintViolationException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		String message = null;
		for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
			message = constraintViolation.getMessage();
			break;
		}
		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", message));
	}

	@ExceptionHandler({SQLIntegrityConstraintViolationException.class})
	public ErrorResponse handlePersistenceException(final Exception ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		final Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);
		String message = "系统异常";
		if (cause.getMessage().contains("Duplicate entry")) {
			final String[] split = cause.getMessage().split(" ");
			message = split[2] + "已存在";
		}

		return ErrorResponse.of(ResultCode.BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", message));
	}

	/**
	 * 处理 Spring Security 权限不足的异常
	 * <p>
	 * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
	 */
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	public ErrorResponse handleAccessDeniedException(final AccessDeniedException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);
		return ErrorResponse.of(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
	}

	/**
	 * 处理 SpringMVC 请求地址不存在
	 * <p>
	 * 注意，它需要设置如下两个配置项：
	 * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
	 * 2. spring.mvc.static-path-pattern 为 /statics/**
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);

		return ErrorResponse.of(ResultCode.NOT_FOUND.getCode(), String.format("请求地址不存在: %s", ex.getRequestURL()));
	}

	@ExceptionHandler({BusinessException.class})
	public ErrorResponse handleBusinessException(final BusinessException ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);
		return ErrorResponse.of(INTERNAL_ERROR.getCode(), ex.getMessage());
	}

	@ExceptionHandler({Exception.class})
	public ErrorResponse handleException(final Exception ex, final @NonNull ServletWebRequest request) {
		logException(ex, request);
		return ErrorResponse.of(INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getMessage());
	}

	/**
	 * @param exception
	 * @see <a href="https://github.com/jhipster/jhipster-lite/blob/main/src/main/java/tech/jhipster/lite/shared/error/infrastructure/primary/GeneratorErrorsHandler.java">GeneratorErrorsHandler.java</a>
	 */
	private void logException(Throwable exception, ServletWebRequest request) {
		log.error("Processing exception for {}", request.getRequest().getRequestURI(), exception);
	}
}
