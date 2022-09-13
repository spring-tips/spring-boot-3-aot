package bootiful.aot.javapoet;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
class JavaPoetConfigurationTest {

	record Customer(Integer id, String name) {
	}

	static class CustomerService {

		private final Map<Integer, Customer> db = Map.of(1, new Customer(1, "Jürgen"));

		Customer save(Integer id, String name) {
			return this.db.put(id, new Customer(id, name));
		}

		Customer byId(Integer id) {
			return this.db.get(id);
		}

	}

	@Test
	void test() {
		var typeSpec = subclassFor(CustomerService.class);
		var javaFile = JavaFile//
				.builder(CustomerService.class.getPackageName(), typeSpec)//
				.build();
		var javaCode = javaFile.toString();
		log.info("java: " + javaCode);
	}

	private MethodSpec buildSubclassMethodFor(Method method) {
		var newMethodDefinition = MethodSpec.methodBuilder(method.getName())//
				.addModifiers(Modifier.PUBLIC)//
				.returns(method.getReturnType())//
				.addAnnotation(Override.class);

		var paramNames = Stream //
				.of(method.getParameters())//
				.map(parameter -> {//
					var parameterName = parameter.getName();//
					newMethodDefinition.addParameter(parameter.getType(), parameterName);
					return parameterName;
				}).toList();
		var returnStatement = String.format("%s super.$L($L)".trim(),
				method.getReturnType().equals(Void.class) ? "" : " return");
		newMethodDefinition.addStatement(returnStatement, method.getName(),
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