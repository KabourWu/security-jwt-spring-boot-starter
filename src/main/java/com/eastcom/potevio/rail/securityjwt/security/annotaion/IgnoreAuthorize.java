package com.eastcom.potevio.rail.securityjwt.security.annotaion;

import java.lang.annotation.*;

/**
 * 指定接口忽略认证，可以直接访问
 *
 * @author kabour
 * @date 2019/7/31 15:43
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuthorize {
}
