package com.chensoul.oauth2.resource.controller;

import com.chensoul.oauth2.common.util.RestResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Comment
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping
public class UserController {
	@GetMapping("/user")
	public RestResponse user(Authentication authentication) {
		return RestResponse.ok(authentication.getPrincipal());
	}
}
