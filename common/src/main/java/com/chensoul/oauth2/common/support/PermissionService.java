package com.chensoul.oauth2.common.support;

import com.chensoul.oauth2.common.util.SecurityUser;
import com.chensoul.oauth2.common.util.SecurityUtils;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

/**
 * 接口权限判断工具
 */
@Slf4j
public class PermissionService {
  /**
   * 所有权限标识
   */
  private static final String ALL_PERMISSION = "*:*:*";
  private static final String DELIMITER = ",";
  private static final String SUPER_ADMIN = "ROLE_admin";


  /**
   * 判断接口是否有xxx:xxx权限
   *
   * @param permission 权限
   * @return {boolean}
   */
  public boolean hasPermission(String permission) {
    return hasAnyPermission(permission);
  }

  /**
   * 验证用户是否不具备某权限，与 hasPermission 逻辑相反
   *
   * @param permission 权限字符串
   * @return 用户是否不具备某权限
   */
  public boolean lacksPermission(String permission) {
    return hasPermission(permission) != true;
  }

  /**
   * 验证用户是否具有以下任意一个权限
   *
   * @param permissions 以 DELIMITER 为分隔符的权限列表
   * @return 用户是否具有以下任意一个权限
   */
  public boolean hasAnyPermission(String permissions) {
    if (StringUtils.isEmpty(permissions)) {
      return false;
    }
    SecurityUser loginUser = SecurityUtils.getCurrentUser();
    if (StringUtils.isEmpty(loginUser) || CollectionUtils.isEmpty(loginUser.getAuthorities())) {
      return false;
    }
    Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities();
    for (String permission : permissions.split(DELIMITER)) {
      if (permission != null && hasPermissions(authorities, permission)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断用户是否拥有某个角色
   *
   * @param role 角色字符串
   * @return 用户是否具备某角色
   */
  public boolean hasRole(String role) {
    if (StringUtils.isEmpty(role)) {
      return false;
    }
    SecurityUser loginUser = SecurityUtils.getCurrentUser();
    if (StringUtils.isEmpty(loginUser) || CollectionUtils.isEmpty(loginUser.getAuthorities())) {
      return false;
    }
    for (GrantedAuthority authorities : loginUser.getAuthorities()) {
      String roleKey = authorities.getAuthority();
      if (SUPER_ADMIN.contains(roleKey) || roleKey.contains(role)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 验证用户是否不具备某角色，与 hasRole 逻辑相反。
   *
   * @param role 角色名称
   * @return 用户是否不具备某角色
   */
  public boolean lacksRole(String role) {
    return hasRole(role) != true;
  }

  /**
   * 验证用户是否具有以下任意一个角色
   *
   * @param roles 以 DELIMITER 为分隔符的角色列表
   * @return 用户是否具有以下任意一个角色
   */
  public boolean hasAnyRoles(String roles) {
    if (StringUtils.isEmpty(roles)) {
      return false;
    }
    SecurityUser loginUser = SecurityUtils.getCurrentUser();
    if (StringUtils.isEmpty(loginUser) || CollectionUtils.isEmpty(loginUser.getAuthorities())) {
      return false;
    }
    for (String role : roles.split(DELIMITER)) {
      if (hasRole(role)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断是否包含权限
   *
   * @param authorities 权限列表
   * @param permission  权限字符串
   * @return 用户是否具备某权限
   */
  private boolean hasPermissions(Collection<? extends GrantedAuthority> authorities, String permission) {
    return authorities.stream().map(GrantedAuthority::getAuthority).filter(StringUtils::hasText).anyMatch(x -> ALL_PERMISSION.contains(x) || PatternMatchUtils.simpleMatch(permission, x));
  }
}
