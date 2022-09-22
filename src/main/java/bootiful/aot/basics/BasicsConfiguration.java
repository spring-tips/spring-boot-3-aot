package bootiful.aot.basics;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.stream.Stream;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
class BasicsConfiguration {

	@Bean
	ApplicationListener<ApplicationReadyEvent> orderDataApplicationListener(OrderRepository repository) {
		return event -> repository
				.saveAll(Stream.of("r593r", "224934923", "9kdh972").map(s -> new OrderItem(null, s)).toList());
	}

	@Bean
	RouterFunction<ServerResponse> routes(OrderRepository repository) {
		return route().GET("/orders", request -> ServerResponse.ok().body(repository.findAll())).build();
	}

}

record OrderItem(@Id Integer id, String sku) {
}

interface OrderRepository extends CrudRepository<OrderItem, Integer> {

}