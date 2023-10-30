package com.eastcom.potevio.rail.securityjwt.security.authority;

import com.eastcom.potevio.rail.securityjwt.security.annotaion.ApiAuthority;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * 查找项目中加了 @ApiAuthority 的方法，并调用业务逻辑功能
 *
 * @author kabour
 * @date 2019/7/12 21:05
 */
public class ApiAuthorityBeanPostProcessor extends AbstractAdvisingBeanPostProcessor implements InitializingBean {
    public static final Logger LOGGER = LoggerFactory.getLogger(ApiAuthorityBeanPostProcessor.class);

    @Autowired(required = false)
    private MemberApiAuthorityHandler memberApiAuthorityHandler;
    @Autowired(required = false)
    private AdministratorApiAuthorityHandler administratorApiAuthorityHandler;

    public ApiAuthorityBeanPostProcessor(Advisor advisor) {
        this.advisor = advisor;
        this.beforeExistingAdvisors = true;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ApiAuthority apiAuthority = AnnotationUtils.findAnnotation(method, ApiAuthority.class);
            if (apiAuthority != null) {
                LOGGER.debug("{}.{} 扫描到注解@ApiAuthority", bean.getClass().getSimpleName(), method.getName());
                String authorityName = apiAuthority.name();
                if (StringUtils.isBlank(authorityName)) {
                    continue;
                }
                authorityName = authorityName.trim();
                LOGGER.debug("{}.{} 进行权限记录", bean.getClass().getSimpleName(), method.getName());
                ApiAuthority.Type type = apiAuthority.type();
                if (type == ApiAuthority.Type.MEMBER || type == ApiAuthority.Type.MEMBER_AND_ADMIN) {
                    if (memberApiAuthorityHandler != null)
                        memberApiAuthorityHandler.handle(authorityName, apiAuthority.description());
                }
                if (type == ApiAuthority.Type.ADMIN || type == ApiAuthority.Type.MEMBER_AND_ADMIN) {
                    if (administratorApiAuthorityHandler != null)
                        administratorApiAuthorityHandler.handle(authorityName, apiAuthority.description());
                }

            }
        }
        return bean;
    }

    @Override
    public void afterPropertiesSet() {
        if (memberApiAuthorityHandler == null) {
            LOGGER.info("未配置@ApiAuthority注解MEMBER类型数据记录hadler");
        } else {
            LOGGER.info("配置了@ApiAuthority注解MEMBER类型数据记录hadler，相关数据能被处理");
        }
        if (administratorApiAuthorityHandler == null) {
            LOGGER.info("未配置@ApiAuthority注解ADMIN类型数据记录hadler");
        } else {
            LOGGER.info("配置了@ApiAuthority注解ADMIN类型数据记录hadler，相关数据能被处理");
        }
    }
}
