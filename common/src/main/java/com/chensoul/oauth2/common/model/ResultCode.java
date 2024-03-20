package com.chensoul.oauth2.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result code
 *
 * @author chensoul
 * @since 4.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(0, "SUCCESS"),

    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),

    SYSTEM_ERROR(500, "INTERNAL_ERROR"),
    SERVICE_ERROR(501, "SERVICE_ERROR"),
    SERVICE_UNAVAILABLE(502, "SERVICE_UNAVAILABLE"),
    ;

    private int code;
    private String name;

}
