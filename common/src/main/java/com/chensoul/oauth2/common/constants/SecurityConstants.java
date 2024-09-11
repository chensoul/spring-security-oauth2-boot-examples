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
package com.chensoul.oauth2.common.constants;

public interface SecurityConstants {
  /**
   * 角色前缀
   */
  String ROLE = "ROLE_";

  /**
   * 内部
   */
  String FROM_IN = "Y";

  /**
   * 标志
   */
  String FROM = "from";


  /**
   * OAUTH2 令牌类型 https://oauth.net/2/bearer-tokens/
   */
  String OAUTH2_TOKEN_TYPE = "bearer ";

  String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, " + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, " + "refresh_token_validity, additional_information, autoapprove";

  /**
   * JdbcClientDetailsService 查询语句
   */
  String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from oauth_client_details";

  /**
   * 默认的查询语句
   */
  String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";

  /**
   * 按条件client_id 查询
   */
  String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

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

  String GRANT_TYPE = "grant_type";

  String JWT_USER_ID = "user_id";

  String JWT_NICKNAME = "nick_name";

  String JWT_USERNAME = "user_name";

  String JWT_AUTHORITIES = "authorities";

  String JWT_TENANT_ID = "tenant_id";

  String JWT_CLIENT_ID = "client_id";

  String JWT_SCOPE = "scope";

  String JWT_AUTHORITY = "authority";

}
