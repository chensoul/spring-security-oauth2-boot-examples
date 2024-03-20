--------------- H2 ---------------

drop table if exists oauth_client_details;
create table oauth_client_details (
  client_id VARCHAR(255) PRIMARY KEY,
  resource_ids VARCHAR(255),
  client_secret VARCHAR(255),
  scope VARCHAR(255),
  authorized_grant_types VARCHAR(255),
  web_server_redirect_uri VARCHAR(255),
  authorities VARCHAR(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(255)
);

drop table if exists oauth_client_token;
create table oauth_client_token (
  token_id VARCHAR(255),
  token LONGVARBINARY,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255)
);

drop table if exists oauth_access_token;
create table oauth_access_token (
  token_id VARCHAR(255),
  token LONGVARBINARY,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication LONGVARBINARY,
  refresh_token VARCHAR(255)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token (
  token_id VARCHAR(255),
  token LONGVARBINARY,
  authentication LONGVARBINARY
);

drop table if exists oauth_code;
create table oauth_code (
  code VARCHAR(255), authentication LONGVARBINARY
);

drop table if exists oauth_approvals;
create table oauth_approvals (
	userId VARCHAR(255),
	clientId VARCHAR(255),
	scope VARCHAR(255),
	status VARCHAR(10),
	expiresAt TIMESTAMP,
	lastModifiedAt TIMESTAMP
);

drop table if exists USERS;

CREATE TABLE USERS (ID INT PRIMARY KEY, NAME VARCHAR(45), USERNAME VARCHAR(45), PASSWORD VARCHAR(60));

----------------- MySQL ---------------
--DROP TABLE IF EXISTS `oauth_access_token`;
--CREATE TABLE `oauth_access_token`  (
--  `token_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `token` blob NULL,
--  `authentication_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
--  `user_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `client_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `authentication` blob NULL,
--  `refresh_token` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  PRIMARY KEY (`authentication_id`) USING BTREE
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
--DROP TABLE IF EXISTS `oauth_client_details`;
--CREATE TABLE `oauth_client_details`  (
--  `client_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端。\r\n未查询到：\r\n{\r\n    \"error\": \"invalid_client\",\r\n    \"error_description\": \"Bad client credentials\"\r\n}',
--  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端秘钥，bcrypt 加密',
--  `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限。\r\n为空：\r\n{\r\n  \"error\": \"invalid_scope\",\r\n  \"error_description\": \"Empty scope (either the client or the user is not allowed the requested scopes)\"\r\n}\r\n不匹配：\r\n{\r\n    \"error\": \"invalid_scope\",\r\n    \"error_description\": \"Invalid scope: snsapi_base\"\r\n}',
--  `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权类型。\r\n为空：authorization_code,refresh_token（参见：org.springframework.security.oauth2.provider.client.BaseClientDetails#BaseClientDetails(String, String, String, String, String, String)）\r\n范围：authorization_code,refresh_token,client_credentials,password',
--  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '重定向URI。\r\n为空：\r\n{\r\n    \"error\": \"invalid_request\",\r\n    \"error_description\": \"At least one redirect_uri must be registered with the client.\"\r\n}\r\n未匹配到：\r\n{\r\n    \"error\": \"invalid_grant\",\r\n    \"error_description\": \"Invalid redirect: http://127.0.0.1:123 does not match one of the registered values.\"\r\n}',
--  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `access_token_validity` int NULL DEFAULT NULL COMMENT 'token 有效时间，单位：秒。默认值：43200秒（12小时，参见：org.springframework.security.oauth2.provider.token.DefaultTokenServices#accessTokenValiditySeconds）',
--  `refresh_token_validity` int NULL DEFAULT NULL COMMENT '刷新 token 有效时间，单位：秒。默认值：2592000（30天，参见：org.springframework.security.oauth2.provider.token.DefaultTokenServices#refreshTokenValiditySeconds）',
--  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自动授权\r\n全部自动授权：true\r\n指定范围自动授权：填写scope',
--  PRIMARY KEY (`client_id`) USING BTREE
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
--DROP TABLE IF EXISTS `oauth_code`;
--CREATE TABLE `oauth_code`  (
--  `code` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `authentication` blob NULL
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
--
--DROP TABLE IF EXISTS `oauth_refresh_token`;
--CREATE TABLE `oauth_refresh_token`  (
--  `token_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--  `token` blob NULL,
--  `authentication` blob NULL
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
--DROP TABLE IF EXISTS `authorities`;
--CREATE TABLE `authorities`  (
--  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
--  `authority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限',
--  UNIQUE INDEX `ix_auth_username`(`username`, `authority`) USING BTREE,
--  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
--
--DROP TABLE IF EXISTS `users`;
--CREATE TABLE `users`  (
--   id bigint(20) NOT NULL AUTO_INCREMENT,
--  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
--  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
--  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码，bcrypt 加密',
--  `enabled` tinyint(1) NOT NULL COMMENT '是否启用',
--  PRIMARY KEY (`id`)
--) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ;
--
