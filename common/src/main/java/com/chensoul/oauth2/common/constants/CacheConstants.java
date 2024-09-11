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

/**
 * 缓存的key 常量
 */
public interface CacheConstants {
	/**
	 * 菜单信息缓存的key
	 */
	String MENU_DETAIL = "cocktail:menu_detail";

	/**
	 * 用户信息缓存的key
	 */
	String USER_DETAIL = "cocktail:user_detail";

	/**
	 * 字典信息缓存的key
	 */
	String DICT_DETAIL = "cocktail:dict_detail";

	/**
	 * oauth 客户端缓存的key，值为hash
	 */
	String OAUTH_CLIENT_DETAIL = "cocktail:oauth_client_detail";

	/**
	 * tokens缓存的key
	 */
	String OAUTH_TOKENS = "cocktail:oauth_tokens";

	/**
	 * oauth token store 存储前缀
	 */
	String OAUTH_TOKEN_STORE_PREFIX = "cocktail:oauth_token_store:";

	String SHOP = "cocktail:shop";

	String TENANT_APP_CONFIG = "cocktail:tenant_app_config";

	String CLIENT = "cocktail:client";

	String LOGIN_FAIL_COUNT_CACHE = "cocktail:loginFailCount:";
}
