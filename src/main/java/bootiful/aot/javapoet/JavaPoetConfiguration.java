package bootiful.aot.javapoet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Configuration
class JavaPoetConfiguration {

	record Customer(Integer id, String name) {
	}

	class CustomerService {

		private final Map<Integer, Customer> customerMap = Map.of(1, new Customer(1, "Jürgen"));

		Customer byId(Integer id) {
			return this.customerMap.get(id);
		}

	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> javaPoetListener() {
		return event -> {
			var clazzToSubclass = CustomerService.class;
			var typeSpec = subclassFor(clazzToSubclass);
			var javaFile = JavaFile.builder(clazzToSubclass.getPackageName(), typeSpec).build();
			log.info(javaFile.toString());
		};
	}

	private MethodSpec buildSubclassMethodFor(Method method) {
		var newMethodDefinition = MethodSpec.methodBuilder(method.getName())//
				.addModifiers(Modifier.PUBLIC)//
				.returns(method.getReturnType())//
				.addAnnotation(Override.class);
		var paramNames = new ArrayList<String>();
		Stream.of(method.getParameters()).forEach(parameter -> {
			paramNames.add(parameter.getName());
			newMethodDefinition.addParameter(parameter.getType(), parameter.getName());
		});
		var javaMethodBodyCode = String.format("   %s super.$L($L) ".trim(),
				method.getReturnType().equals(Void.class) ? "" : " return");
		newMethodDefinition.addStatement(javaMethodBodyCode, method.getName(),
				StringUtils.collectionToDelimitedString(paramNames, ","));
		return newMethodDefinition.build();
	}

	private TypeSpec subclassFor(Class<?> target) {
		Assert.state(!java.lang.reflect.Modifier.isFinal(target.getModifiers()),
				"we can't subclass a type that's final!");
		var methods = Stream //
				.of(ReflectionUtils.getUniqueDeclaredMethods(target)) //
				.filter(m -> !java.lang.reflect.Modifier.isPrivate(m.getModifiers())
						&& !ReflectionUtils.isObjectMethod(m)) //
				.map(this::buildSubclassMethodFor) //
				.toList();
		var newType = TypeSpec //
				.classBuilder(target.getSimpleName() + "__Proxy") //
				.addModifiers(Modifier.PUBLIC);
		Stream.of(target.getInterfaces()).forEach(newType::addSuperinterface);
		newType.superclass(target);
		methods.forEach(newType::addMethod);
		return newType.build();
	}

}
