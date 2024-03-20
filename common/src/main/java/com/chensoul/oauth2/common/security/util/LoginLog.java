package com.chensoul.oauth2.common.security.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class LoginLog {

    private static final long serialVersionUID = 1L;

    private String username;

    private String token;

    private String loginIp;

    private String loginLocation;

    private String browserType;

    private String osType;

    private Integer success;

    private String clientId;

    private String grantType;

    private String traceId;

    private String tenantId;
}
