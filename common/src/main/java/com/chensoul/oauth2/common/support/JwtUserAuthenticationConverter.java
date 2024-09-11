package com.chensoul.oauth2.common.support;

import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_AUTHORITIES;
import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_NICKNAME;
import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_USERNAME;
import static com.chensoul.oauth2.common.constants.SecurityConstants.JWT_USER_ID;
import com.chensoul.oauth2.common.util.SecurityUser;
import com.chensoul.oauth2.common.util.User;
import com.chensoul.oauth2.common.util.UserPrincipal;
import static com.chensoul.oauth2.common.util.UserPrincipal.Type.USER_NAME;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

/**
 * 从 jwt 中解析用户信息
 */
public class JwtUserAuthenticationConverter extends DefaultUserAuthenticationConverter implements UserAuthenticationConverter {
  protected static final String N_A = "N/A";

  /**
   * Inverse of {@link #convertUserAuthentication(Authentication)}. Extracts an Authentication from a map.
   *
   * @param map a map of user information
   * @return an Authentication representing the user or null if there is none
   */
  @Override
  public Authentication extractAuthentication(Map<String, ?> map) {
    return doEextractAuthentication(map);
  }

  protected Authentication doEextractAuthentication(Map<String, ?> map) {
    if (map.containsKey(JWT_USERNAME)) {
      Collection<? extends GrantedAuthority> authorities = getAuthorities(map);

      Integer id = (Integer) map.get(JWT_USER_ID);
      String username = (String) map.get(JWT_USERNAME);
      String nickName = (String) map.get(JWT_NICKNAME);

      User user = new User();
      user.setId(Long.valueOf(id));
      user.setUsername(username);
      user.setNickName(nickName);

      SecurityUser securityUser = new SecurityUser(user, N_A, new UserPrincipal(USER_NAME, username));
      return new UsernamePasswordAuthenticationToken(securityUser, N_A, authorities);
    }
    return null;
  }

  protected Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
    Object authorities = map.get(JWT_AUTHORITIES);
    if (Objects.isNull(authorities)) {
      return AuthorityUtils.NO_AUTHORITIES;
    }
    if (authorities instanceof String) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
    }
    if (authorities instanceof Collection) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
    }
    throw new IllegalArgumentException("Authorities must be either a String or a Collection");
  }
}
