package bootiful.aot.brap.codegen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.javapoet.CodeBlock;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.time.Instant;

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
