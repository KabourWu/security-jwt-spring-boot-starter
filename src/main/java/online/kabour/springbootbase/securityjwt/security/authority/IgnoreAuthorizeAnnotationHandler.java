package online.kabour.springbootbase.securityjwt.security.authority;

import online.kabour.springbootbase.securityjwt.component.AnnotationScanner;
import online.kabour.springbootbase.securityjwt.security.annotaion.IgnoreAuthorize;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kabour
 * @date 2019/7/31 20:32
 */
public class IgnoreAuthorizeAnnotationHandler implements AnnotationScanner.MethodAnnotationHandler<IgnoreAuthorize> {

	private List<Object[]> manyToManyEndpointList = new ArrayList<>();

	@Override
	public void handle(IgnoreAuthorize annotation, Method method) {
		Object[] objects = findMethodUrl(method);
		if (objects != null) {
			manyToManyEndpointList.add(objects);
		}
	}

	public List<Object[]> getIgnoreEndpoints() {
		return transferOneToMany(manyToManyEndpointList);
	}

	/**
	 * 找到方法上的 url， 请求method
	 *
	 * @param method
	 * @return
	 */
	private Object[] findMethodUrl(Method method) {
		RequestMapping classRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
		String clazzUrl = "";
		if (classRequestMapping != null && classRequestMapping.value().length > 0) {
			clazzUrl = classRequestMapping.value()[0];
		}
		if (classRequestMapping != null && classRequestMapping.path().length > 0) {
			clazzUrl = classRequestMapping.path()[0];
		}
		String finalClazzUrl = clazzUrl;
		Object[] result = new Object[2];
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		if (requestMapping != null) {
			String[] urls = requestMapping.value();
			if (urls.length == 0) {
				urls = requestMapping.path();
			}
			RequestMethod[] methods = requestMapping.method();
			result[0] = methods;
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		GetMapping getMapping = method.getAnnotation(GetMapping.class);
		if (getMapping != null) {
			String[] urls = getMapping.value();
			if (urls.length == 0) {
				urls = getMapping.path();
			}
			result[0] = new RequestMethod[]{RequestMethod.GET};
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		PostMapping postMapping = method.getAnnotation(PostMapping.class);
		if (postMapping != null) {
			String[] urls = postMapping.value();
			if (urls.length == 0) {
				urls = postMapping.path();
			}
			result[0] = new RequestMethod[]{RequestMethod.POST};
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		PutMapping putMapping = method.getAnnotation(PutMapping.class);
		if (putMapping != null) {
			String[] urls = putMapping.value();
			if (urls.length == 0) {
				urls = putMapping.path();
			}
			result[0] = new RequestMethod[]{RequestMethod.PUT};
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
		if (deleteMapping != null) {
			String[] urls = deleteMapping.value();
			if (urls.length == 0) {
				urls = deleteMapping.path();
			}
			result[0] = new RequestMethod[]{RequestMethod.DELETE};
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
		if (patchMapping != null) {
			String[] urls = patchMapping.value();
			if (urls.length == 0) {
				urls = patchMapping.path();
			}
			result[0] = new RequestMethod[]{RequestMethod.PATCH};
			result[1] = Arrays.stream(urls).map(url -> finalClazzUrl + url).collect(Collectors.toList()).toArray(urls);
			return result;
		}
		return null;
	}

	/**
	 * 将RequestMethod，url 多对多转换成 HttpMethod,url 一对多
	 *
	 * @param manyToManyList
	 * @return
	 */
	private List<Object[]> transferOneToMany(List<Object[]> manyToManyList) {
		List<Object[]> oneToManyList = manyToManyList.stream().map(objects -> {
			Object[] newObjects = new Object[2];
			RequestMethod[] requestMethods = (RequestMethod[]) objects[0];
			if (requestMethods == null) {
				newObjects[0] = null;
				newObjects[1] = objects[1];
				return newObjects;
			}
			for (RequestMethod requestMethod : requestMethods) {
				newObjects[0] = requestMethodMappingHttpMethod(requestMethod);
				newObjects[1] = objects[1];
				return newObjects;
			}
			return newObjects;
		}).collect(Collectors.toList());
		return oneToManyList;
	}

	/**
	 * RequestMethod 到 HttpMethod 的映射
	 *
	 * @param requestMethod
	 * @return
	 */
	private HttpMethod requestMethodMappingHttpMethod(RequestMethod requestMethod) {
		HttpMethod httpMethod = null;
		switch (requestMethod) {
			case GET:
				httpMethod = HttpMethod.GET;
				break;
			case PUT:
				httpMethod = HttpMethod.PUT;
				break;
			case HEAD:
				httpMethod = HttpMethod.HEAD;
				break;
			case POST:
				httpMethod = HttpMethod.POST;
				break;
			case PATCH:
				httpMethod = HttpMethod.PATCH;
				break;
			case TRACE:
				httpMethod = HttpMethod.TRACE;
				break;
			case DELETE:
				httpMethod = HttpMethod.DELETE;
				break;
			case OPTIONS:
				httpMethod = HttpMethod.OPTIONS;
				break;
		}
		return httpMethod;
	}
}
