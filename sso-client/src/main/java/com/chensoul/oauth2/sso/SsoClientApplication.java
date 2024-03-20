package com.chensoul.oauth2.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TODO
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@SpringBootApplication
public class SsoClientApplication implements WebMvcConfigurer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SsoClientApplication.class, args);
	}

}
