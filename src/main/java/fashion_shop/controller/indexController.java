package fashion_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("index")
public class indexController {
	public String index() {
		return "index";
	}
}
