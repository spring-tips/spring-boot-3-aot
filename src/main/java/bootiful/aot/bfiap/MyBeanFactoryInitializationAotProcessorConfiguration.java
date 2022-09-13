package bootiful.aot.bfiap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.javapoet.CodeBlock;

import javax.lang.model.element.Modifier;

@Slf4j
@Configuration
class MyBeanFactoryInitializationAotProcessorConfiguration {

	@Bean
	BeanFactoryInitializationAotProcessor aotProcessor() {
		return beanFactory -> (ctx, code) -> {
			var generatedMethod = code.getMethods() //
					.add("registerMyPrintln", (method) -> {
						method.addJavadoc("Register a println()");
						method.addModifiers(Modifier.STATIC, Modifier.PUBLIC);
						method.addParameter(DefaultListableBeanFactory.class, "beanFactory");
						method.addCode(CodeBlock.builder()
								.addStatement(("  System.out.println(\"Hello, world from "
										+ MyBeanFactoryInitializationAotProcessorConfiguration.class.getName()
										+ "!\")  ").trim())
								.build());
					});
			code.addInitializer(generatedMethod.toMethodReference());
		};
	}

}
