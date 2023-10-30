package com.eastcom.potevio.rail.securityjwt.security.authority;

import com.eastcom.potevio.rail.securityjwt.security.PrincipalHelper;
import com.eastcom.potevio.rail.securityjwt.security.annotaion.ApiAuthority;
import com.eastcom.potevio.rail.securityjwt.security.auth.JwtAuthenticationToken;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * @author kabour
 * @date 2019/7/13 19:24
 */
public class ApiAuthorityMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        ApiAuthority apiAuthority = AnnotationUtils.findAnnotation(methodInvocation.getMethod(), ApiAuthority.class);
        if (apiAuthority != null) {
            LoginRequest.UserType principalType = PrincipalHelper.principalType();
            if (principalType == null || principalType == LoginRequest.UserType.ANONYMOUS) {
                return methodInvocation.proceed();
            }
            if (principalType == LoginRequest.UserType.ADMIN) {
                if (apiAuthority.type() == ApiAuthority.Type.MEMBER) {
                    throw new AccessDeniedException("权限不足，无法访问！");
                }
            }
            if (principalType == LoginRequest.UserType.MEMBER) {
                if (apiAuthority.type() == ApiAuthority.Type.ADMIN) {
                    throw new AccessDeniedException("权限不足，无法访问！");
                }
            }

            //判断具体权限
            String apiAuthorityName = apiAuthority.name();
            if (!StringUtils.isBlank(apiAuthorityName)) {
                JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                UserContext userContext = (UserContext) authentication.getPrincipal();
                Collection<GrantedAuthority> grantedAuthorities = userContext.getAuthorities();
                if (!grantedAuthorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(apiAuthority.name().trim()))) {
                    throw new AccessDeniedException("权限不足，无法访问！");
                }
            }

        }
        Object rest = methodInvocation.proceed();
        return rest;
    }
}
