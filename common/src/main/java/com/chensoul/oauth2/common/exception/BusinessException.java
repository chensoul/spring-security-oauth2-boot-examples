package com.chensoul.oauth2.common.exception;

import com.chensoul.oauth2.common.model.ResultCode;
import lombok.Getter;

/**
 * Business exception
 * <p>
 * 强制业务异常必须提供code码，便于统一维护
 *
 * @author chensoul
 * @since 4.0.0
 */

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 6240659073530676090L;
    @Getter
    private final int code;

    public BusinessException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(final ResultCode resultCode) {
        super(resultCode.getName());
        code = resultCode.getCode();
    }

    public BusinessException(final ResultCode resultCode, final String message) {
        super(message);
        code = resultCode.getCode();
    }
}
