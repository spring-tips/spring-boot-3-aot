package bootiful.aot.bfiap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.javapoet.CodeBlock;

import javax.lang.model.element.Modifier;

import static org.springframework.javapoet.CodeBlock.builder;

@Slf4j
@Configuration
class MyBeanFactoryInitializationAotProcessorConfiguration {

	@Bean
	BeanFactoryInitializationAotProcessor aotProcessor() {
		var className = MyBeanFactoryInitializationAotProcessorConfiguration.class.getName();
		return beanFactory -> (ctx, code) -> {
			var codeBlock = builder().addStatement("System.out.println(\"Hello, world from $L!\")  ", className)
					.build();
			var generatedMethod = code//
					.getMethods() //
					.add("registerPrintln", (method) -> {
						method.addJavadoc("Register a println()");
						method.addModifiers(Modifier.STATIC, Modifier.PUBLIC);
						method.addParameter(DefaultListableBeanFactory.class, "beanFactory");
						method.addCode(codeBlock);
					});
			code.addInitializer(generatedMethod.toMethodReference());
		};
	}

}
