package bootiful.aot.brap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.javapoet.CodeBlock;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
class CompilationEndpointConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(CompilationEndpoint endpoint) {
		return event -> log.info(endpoint.compilation().toString());
	}

	@Bean
	CompilationEndpoint compilationEndpoint() {
		return new CompilationEndpoint();
	}

	@Bean
	CompilationAotProcessor compilationAotProcessor() {
		return new CompilationAotProcessor();
	}

}

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
