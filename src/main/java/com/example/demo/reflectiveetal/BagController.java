package com.example.demo.reflectiveetal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
class BagController {

	private final Bag bag;

	BagController(Bag bag) {
		this.bag = bag;
	}

	@GetMapping("/bag")
	ID read() {
		return this.bag.getUuid();
	}

}
