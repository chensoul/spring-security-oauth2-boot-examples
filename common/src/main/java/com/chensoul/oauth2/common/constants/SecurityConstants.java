package com.chensoul.oauth2.common.constants;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
public interface SecurityConstants {
    String JWT_CLIENT_ID = "client_id";
    String JWT_USERNAME = "username";
    String JWT_NAME = "name";
    String JWT_USER_ID = "user_id";
    String JWT_JTI = "jti";
    String JWT_SIGNING_KEY = "cocktail-cloud@wesine";

    /**
     * 角色前缀
     */
    String ROLE_PREFIX = "ROLE_";
    String ALL_PERMISSION = "*:*:*";

    /**
     * 内部
     */
    String FROM_IN = "Y";

    /**
     * 标志
     */
    String FROM = "from";


    /**
     * header 中租户ID
     */
    String HEADER_TENANT_ID = "TENANT-ID";

    String HEADER_CLIENT_ID = "CLIENT-ID";

    /**
     * Gateway请求头TOKEN名称（不要有空格）
     */
    String HEADER_GATEWAY_TOKEN = "X-Gateway-Token";

    /**
     * Gateway请求头TOKEN值
     */
    String GATEWAY_TOKEN_VALUE = "cocktail:gateway:123456";

    /**
     * sys_client 表的字段
     */
    String CLIENT_FIELDS = "id, CONCAT('{noop}',secret) as client_secret, resource_ids, scope, "
        + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
        + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_client";

    /**
     * 默认的查询语句
     */
    String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by id";

    /**
     * 按条件client_id 查询
     */
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where status=1 and id = ?";

    /**
     * 刷新模式
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 授权码模式
     */
    String AUTHORIZATION_CODE = "authorization_code";

    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 密码模式
     */
    String PASSWORD = "password";

    /**
     * 简化模式
     */
    String IMPLICIT = "implicit";

    String GRANT_TYPE_SMS = "sms";

    String GRANT_TYPE = "grant_type";

    List<String> EXCLUDE_URL = Arrays.asList("/actuator", "/v2/api-docs", "/swagger-ui.html", "/favicon.ico", "/static", "/error");

    /**
     * oauth 客户端缓存的key，值为hash
     */
    String OAUTH_CLIENT_DETAIL = "oauth2:oauth_client_detail";

    /**
     * oauth token store 存储前缀
     */
    String OAUTH_TOKEN_STORE_PREFIX = "oauth2:";

    String BEARER_TYPE = "Bearer";
}
