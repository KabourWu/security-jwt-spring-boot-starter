package com.eastcom.potevio.rail.securityjwt.security.annotaion;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 指定资源权限
 * 加了此注解的资源 可以被系统发现，供超级管理员给 普通管理员 或者 普通会员 分配
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiAuthority {

    /**
     * 资源的类型,默认类型为管理员
     *
     * @return
     */
    @AliasFor("type")
    Type value() default Type.ADMIN;

    /**
     * 资源的类型,默认类型为管理员
     *
     * @return
     */
    @AliasFor("value")
    Type type() default Type.ADMIN;

    /**
     * 资源权限名称，不填写，注解所在的api资源不能被运维人员通过web看见，进行角色分配权限
     *
     * @return
     */
    String name() default "";

    /**
     * 对于接口的描述，方便运维人员通过web控制台知晓此接口的功能
     *
     * @return
     */
    String description() default "";

    enum Type {
        MEMBER,
        ADMIN,
        MEMBER_AND_ADMIN
    }
}
