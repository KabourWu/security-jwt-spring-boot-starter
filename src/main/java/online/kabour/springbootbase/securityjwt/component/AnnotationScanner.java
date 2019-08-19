package online.kabour.springbootbase.securityjwt.component;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

/**
 * 扫描指定package包下面的注解，并进行业务逻辑处理
 *
 * @author kabour
 * @date 2019/7/29 14:40
 */
public class AnnotationScanner<T extends Annotation> {

	public static final Logger LOGGER = LoggerFactory.getLogger(AnnotationScanner.class);
	private static final String RESOURCE_PATTERN = "**/*.class";
	private final Collection<Class<?>> candidates = new LinkedHashSet<>();
	private String[] basePackages;
	private Class<T> annotationClass;
	private ClassAnnotationHandler classAnnotationHandle;
	private FieldAnnotationHandler fieldAnnotationHandle;
	private MethodAnnotationHandler methodAnnotationHandle;

	public AnnotationScanner(Class<T> annotationClass, String... basePackages) {
		this.basePackages = basePackages;
		this.annotationClass = annotationClass;
	}

	/**
	 * 添加扫描的类
	 *
	 * @param candidateClass
	 * @return
	 */
	public AnnotationScanner addCandidate(Class<?> candidateClass) {
		this.candidates.add(candidateClass);
		return this;
	}

	public AnnotationScanner classAnnotationHandle(ClassAnnotationHandler classAnnotationHandle) {
		this.classAnnotationHandle = classAnnotationHandle;
		return this;
	}

	public AnnotationScanner fieldAnnotationHandle(FieldAnnotationHandler fieldAnnotationHandle) {
		this.fieldAnnotationHandle = fieldAnnotationHandle;
		return this;
	}

	public AnnotationScanner methodAnnotationHandle(MethodAnnotationHandler methodAnnotationHandle) {
		this.methodAnnotationHandle = methodAnnotationHandle;
		return this;
	}

	public void scan() {
		candidates.addAll(findCandidateClasses(basePackages, new TypeFilter()));
		if (candidates.isEmpty()) {
			LOGGER.info("扫描指定基础包[{}]时未发现符合条件的基础类", basePackages.toString());
			return;
		}
		if (classAnnotationHandle != null) {
			scanClazz(candidates);
		}
		if (methodAnnotationHandle != null) {
			scanMethod(candidates);
		}
		if (fieldAnnotationHandle != null) {
			scanClazzField(candidates);
		}

	}

	/**
	 * 扫描字段上面的注解
	 *
	 * @param classes
	 */
	private void scanClazzField(Collection<Class<?>> classes) {
		classes.forEach(clazz -> scanClazzField(clazz));
	}

	/**
	 * 扫描字段上面的注解
	 *
	 * @param clazz
	 */
	private void scanClazzField(Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Class<? extends Annotation> finalAnnotationClass = annotationClass;
		Arrays.stream(fields).filter(field -> field.isAnnotationPresent(finalAnnotationClass)).forEach(field -> {
			Annotation annotation = field.getAnnotation(finalAnnotationClass);
			if (annotation != null && fieldAnnotationHandle != null) {
				fieldAnnotationHandle.handle(annotation, field);
			}
		});
	}

	private void scanMethod(Collection<Class<?>> classes) {
		classes.forEach(clazz -> scanMethod(clazz));
	}

	private void scanMethod(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		Class<? extends Annotation> finalAnnotationClass = annotationClass;
		Arrays.stream(methods).filter(method -> method.isAnnotationPresent(finalAnnotationClass)).forEach(method -> {
			Annotation annotation = method.getAnnotation(finalAnnotationClass);
			if (annotation != null && methodAnnotationHandle != null) {
				methodAnnotationHandle.handle(annotation, method);
			}
		});
	}

	/**
	 * 扫描类上面的注解
	 *
	 * @param classes
	 */
	private void scanClazz(Collection<Class<?>> classes) {
		classes.forEach(clazz -> scanClazz(clazz));
	}

	/**
	 * 扫描类上面的注解
	 *
	 * @param clazz
	 */
	private void scanClazz(Class clazz) {
		Annotation annotation = clazz.getAnnotation(this.annotationClass);
		if (annotation != null && classAnnotationHandle != null) {
			classAnnotationHandle.handle(annotation, clazz);
		}
	}

	/**
	 * 扫描包下面所有的类
	 *
	 * @param packages
	 * @return
	 */
	private Collection<Class<?>> findCandidateClasses(String[] packages, TypeFilter typeFilter) {
		LinkedHashSet<Class<?>> candidates = new LinkedHashSet<>();
		if (packages == null) {
			return candidates;
		}
		Arrays.stream(packages).forEach(pkg -> {
			try {
				candidates.addAll(findCandidateClasses(pkg, typeFilter));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		});
		return candidates;
	}


	/**
	 * 扫描包下面所有的类
	 *
	 * @param basePackage
	 * @return
	 * @throws IOException
	 */
	private List<Class<?>> findCandidateClasses(String basePackage, TypeFilter filter) throws IOException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("开始扫描指定包{}下的所有类", basePackage);
		}
		List<Class<?>> candidates = new ArrayList<>();
		String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + replaceDotByDelimiter(basePackage) + '/' + RESOURCE_PATTERN;
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
		Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
		for (Resource resource : resources) {
			MetadataReader reader = readerFactory.getMetadataReader(resource);
			if (filter.match(reader, readerFactory)) {
				String clazzName = reader.getClassMetadata().getClassName();
				Class<?> candidateClass = getClazzByClazzName(clazzName);
				if (candidateClass != null) {
					candidates.add(candidateClass);
					LOGGER.debug("扫描到符合要求的基础类:{}", candidateClass.getName());
				}
			}
		}
		return candidates;
	}


	/**
	 * 用"/"替换包路径中"."
	 *
	 * @param path
	 * @return
	 */
	private String replaceDotByDelimiter(String path) {
		return StringUtils.replace(path, ".", "/");
	}

	private Class getClazzByClazzName(String clazzName) {
		try {
			return ClassUtils.forName(clazzName, this.getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			LOGGER.info("未找到指定基础类{}", clazzName);
		}
		return null;
	}

	/**
	 * 类级注解数据处理
	 */
	public interface ClassAnnotationHandler<T> {

		default void handle(T annotation, Class clazz) {
			//empty operate
		}
	}

	/**
	 * 属性级注解数据处理
	 */
	public interface FieldAnnotationHandler<T> {

		default void handle(T annotation, Field field) {
			//empty operate
		}
	}

	/**
	 * 方法级注解数据处理
	 */
	public interface MethodAnnotationHandler<T> {

		default void handle(T annotation, Method method) {
			//empty operate
		}
	}

	/**
	 * 扫描类过滤
	 */
	private class TypeFilter extends AbstractClassTestingTypeFilter {
		@Override
		protected boolean match(ClassMetadata metadata) {
			Class clazz = getClazzByClazzName(metadata.getClassName());
			return !metadata.isAbstract() && !metadata.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()
					&& !clazz.isMemberClass() && !clazz.getName().contains("$");
		}
	}


}
