package bootiful.aot.brap.codegen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
