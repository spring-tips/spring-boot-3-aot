package com.example.demo.scopes;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
class BagController {

	private final Bag bag;

	@Lazy
	BagController(Bag bag) {
		this.bag = bag;
	}

	@GetMapping("/bag")
	String read() {
		return this.bag.getUuid();
	}

}
