package online.kabour.springbootbase.securityjwt.security.authority;

import online.kabour.springbootbase.securityjwt.security.annotaion.ApiAuthority;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * @author kabour
 * @date 2019/7/13 20:41
 */
public class ApiAuthorityAnnotationAdvisor extends AbstractPointcutAdvisor {

	private Advice advice;
	private Pointcut pointcut;
	private ApiAuthorityMethodInterceptor apiAuthorityMethodInterceptor;

	public ApiAuthorityAnnotationAdvisor(ApiAuthorityMethodInterceptor apiAuthorityMethodInterceptor) {
		this.apiAuthorityMethodInterceptor = apiAuthorityMethodInterceptor;
		this.advice = buildAdvice();
		this.pointcut = buildPointcut();
	}

	public Pointcut getPointcut() {
		return this.pointcut;
	}

	public Advice getAdvice() {
		return this.advice;
	}

	protected Advice buildAdvice() {
		return apiAuthorityMethodInterceptor;
	}

	protected Pointcut buildPointcut() {
		Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(ApiAuthority.class);
		ComposablePointcut result = new ComposablePointcut(mpc);
		return result;
	}

}
