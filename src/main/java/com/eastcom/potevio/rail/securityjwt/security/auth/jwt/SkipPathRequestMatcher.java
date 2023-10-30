package com.eastcom.potevio.rail.securityjwt.security.auth.jwt;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher matchers;
    private RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(ApplicationContext applicationContext, List<String> pathsToSkip, List<Object[]> mvcPathToSkip, String processingPath) {
        Assert.notNull(pathsToSkip);
        Assert.notNull(mvcPathToSkip);
        List<RequestMatcher> requestMatcherList = pathsToSkip.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
        requestMatcherList.addAll(new MvcRequestMatcherConfigurer(applicationContext).createMvcMatcher(mvcPathToSkip));
        matchers = new OrRequestMatcher(requestMatcherList);
        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (matchers.matches(request)) {
            return false;
        }
        return processingMatcher.matches(request) ? true : false;
    }

    /**
     * 定义Mvc风格的匹配
     */
    public class MvcRequestMatcherConfigurer
            extends AbstractRequestMatcherRegistry<MvcRequestMatcherConfigurer> {

        private MvcRequestMatcherConfigurer(ApplicationContext context) {
            setApplicationContext(context);
        }

        /**
         * 转换成RequestMatcher
         *
         * @param objectsList
         * @return
         */
        public List<RequestMatcher> createMvcMatcher(List<Object[]> objectsList) {
            List<RequestMatcher> requestMatcherList = new ArrayList<>();
            for (Object[] objects : objectsList) {
                HttpMethod httpMethod = (HttpMethod) objects[0];
                String[] urls = (String[]) objects[1];
                requestMatcherList.addAll(super.createMvcMatchers(httpMethod, urls));
            }
            return requestMatcherList;
        }

        @Override
        public MvcRequestMatcherConfigurer mvcMatchers(HttpMethod method,
                                                       String... mvcPatterns) {
            throw new UnsupportedOperationException("不支持此操作");
        }

        @Override
        public MvcRequestMatcherConfigurer mvcMatchers(String... mvcPatterns) {
            throw new UnsupportedOperationException("不支持此操作");
        }

        @Override
        protected MvcRequestMatcherConfigurer chainRequestMatchers(
                List<RequestMatcher> requestMatchers) {
            throw new UnsupportedOperationException("不支持此操作");
        }
    }
}
