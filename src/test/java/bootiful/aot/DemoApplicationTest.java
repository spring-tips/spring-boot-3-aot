package bootiful.aot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@SpringBootTest(classes = DemoApplication.class,
		properties = { "logging.level.root=off", "spring.main.web-application-type=none" })
class DemoApplicationTest {

	private final Environment environment;

	DemoApplicationTest(@Autowired Environment env) {
		this.environment = env;
	}

	@Test
	void userhome() {
		var property = this.environment.getProperty("user.home");
		Assertions.assertTrue(StringUtils.hasText(property));
		System.out.println("user.home: " + property);
	}

}