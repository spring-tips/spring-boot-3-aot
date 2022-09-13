package bootiful.aot.brap;

import bootiful.aot.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DecoratingProxy;
import org.springframework.javapoet.CodeBlock;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.lang.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
class CompilationEndpointConfiguration {

	@Bean
	DefaultProductService productService() {
		return new DefaultProductService();
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> beanPostProcessorProxyListener(ProductService productService) {
		return event -> log.info("saved {}", productService.save(1, "Jürgen").toString());
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> compilationEndpointListener(CompilationEndpoint endpoint) {
		return event -> log.info(Utils.toString(endpoint.compilation()));
	}

	@Bean
	CompilationEndpoint compilationEndpoint() {
		return new CompilationEndpoint();
	}

	@Bean
	CompilationAotProcessor compilationAotProcessor() {
		return new CompilationAotProcessor();
	}

	@Bean
	LoggingBeanPostProcessor loggingBeanPostProcessor() {
		return new LoggingBeanPostProcessor();
	}

}

record Product(Integer id, String sku) {
}

interface ProductService {

	Product save(Integer id, String sku);

}

@Service
@Logged
class DefaultProductService implements ProductService {

	@Override
	public Product save(Integer id, String sku) {
		return new Product(id, sku);
	}

}

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Logged {

}

@Slf4j
class LoggingBeanPostProcessor implements BeanPostProcessor, BeanRegistrationAotProcessor {

	@SneakyThrows
	private static Object proxy(Object object) {
		var pfb = new ProxyFactoryBean();
		pfb.setTarget(object);
		for (var i : object.getClass().getInterfaces())
			pfb.addInterface(i);
		pfb.addAdvice(new MethodInterceptor() {
			@Nullable
			@Override
			public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
				var methodName = invocation.getMethod().getName();
				log.info("before [{}}]", methodName);
				var result = invocation.proceed();
				log.info("after [{}}]", methodName);
				return result;
			}
		});
		return pfb.getObject();
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!matches(bean.getClass())) {
			return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
		}
		return proxy(bean);
	}

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		if (!matches(registeredBean.getBeanClass()))
			return null;
		return (context, code) -> context.getRuntimeHints().proxies().registerJdkProxy(ProductService.class,
				SpringProxy.class, Advised.class, DecoratingProxy.class);
	}

	@Override
	public boolean isBeanExcludedFromAotProcessing() {
		return false;
	}

	private boolean matches(Class<?> beanClazz) {
		return beanClazz != null && beanClazz.getAnnotation(Logged.class) != null;
	}

}

// todo a quick example looking for beans with a marker interface and then creating a
// proxy? that way i could show both code-gen AND hints

@Slf4j
@RequiredArgsConstructor
class CompilationAotProcessor implements BeanRegistrationAotProcessor {

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		if (CompilationEndpoint.class.isAssignableFrom(registeredBean.getBeanClass())) {
			return (ctx, code) -> {
				var generatedClasses = ctx.getGeneratedClasses();
				var generatedClass = generatedClasses.getOrAddForFeatureComponent(
						CompilationEndpoint.class.getSimpleName() + "Component", CompilationEndpoint.class,
						builder -> builder.addModifiers(Modifier.PUBLIC));

				var generatedMethod = generatedClass //
						.getMethods()//
						.add("postProcess", builder -> {
							var outputBeanVariableName = "outputBean";
							builder.addModifiers(Modifier.STATIC, Modifier.PUBLIC)
									.addParameter(RegisteredBean.class, "rb") //
									.addParameter(CompilationEndpoint.class, "inputBean") //
									.returns(CompilationEndpoint.class) //
									.addCode(CodeBlock.builder() //
											.addStatement("""
													$T $L = new $T( $T.ofEpochMilli($L), $S)
													""".stripIndent().trim(), CompilationEndpoint.class,
													outputBeanVariableName, CompilationEndpoint.class, Instant.class,
													System.currentTimeMillis() + "L", new File(".").getAbsolutePath())
											.addStatement("return $L", outputBeanVariableName) //
											.build());
						} //
				);

				var methodReference = generatedMethod.toMethodReference();
				code.addInstancePostProcessor(methodReference);
			};
		}
		return null;
	}

}

@Slf4j
@SuppressWarnings("unused")
@Endpoint(id = "compilation")
class CompilationEndpoint {

	private final Map<String, Object> map = new ConcurrentHashMap<>();

	CompilationEndpoint() {
		this.map.put("message", "No compilation information has been covered");
	}

	// this constructor gets called at AOT time only
	CompilationEndpoint(Instant instant, String directoryOfCompilation) {
		var map = Map.of("datetime", (Object) instant, "directory", (Object) directoryOfCompilation);
		this.map.putAll(map);
	}

	@ReadOperation
	public Map<String, Object> compilation() {
		return Map.of("compilation", this.map, "now", Instant.now());
	}

}
