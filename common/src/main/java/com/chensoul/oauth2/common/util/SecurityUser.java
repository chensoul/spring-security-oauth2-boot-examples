package com.chensoul.oauth2.common.util;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 扩展用户信息
 */
public class SecurityUser extends User implements UserDetails, CredentialsContainer {
	private static final long serialVersionUID = -797397440703066079L;
	private Collection<GrantedAuthority> authorities;

	@Getter
	private UserPrincipal userPrincipal;
	@Getter
	private String sessionId;

	@Setter
	private String password;

	public SecurityUser() {
	}

	public SecurityUser(User user, String password, UserPrincipal userPrincipal) {
		BeanUtils.copyProperties(user, this);
		this.password = password;
		this.userPrincipal = userPrincipal;
		this.sessionId = UUID.randomUUID().toString();
	}

	public Collection<GrantedAuthority> getAuthorities() {
		if (authorities == null) {
			authorities = Stream.of(SecurityUser.this.getAuthority()).map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList());
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public void eraseCredentials() {
		password = null;
	}
}
