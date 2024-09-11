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

public interface EndpointConstant {

	String ALL = "/**";

	String CODE_URL = "/code";

	String OAUTH_ALL = "/oauth/**";

	String OAUTH_AUTHORIZE = "/oauth/authorize";

	String OAUTH_CHECK_TOKEN = "/oauth/check_token";

	String OAUTH_CONFIRM_ACCESS = "/oauth/confirm_access";

	String OAUTH_TOKEN = "/oauth/token";

	String OAUTH_TOKEN_KEY = "/oauth/token_key";

	String OAUTH_ERROR = "/oauth/error";

	String ACTUATOR_ALL = "/actuator/**";

	String TOKEN_CONFIRM_ACCESS = "/token/confirm_access";

	String[] NON_TOKEN_BASED_AUTH_ENTRY_POINTS = new String[]{"/login", OAUTH_AUTHORIZE, "/index.html", "/favicon.ico", "/api/noauth/**", ACTUATOR_ALL, "/error", CODE_URL};

}
