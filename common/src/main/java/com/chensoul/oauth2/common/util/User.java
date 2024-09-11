package com.chensoul.oauth2.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String nickName;

	private Authority authority;

	@JsonIgnore
	private String phone;

	private String email;

	private boolean enabled;
}
