package fashion_shop.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import fashion_shop.entity.Account;
import fashion_shop.entity.Product;

@Transactional
@Controller
@RequestMapping(value = { "/home/", "/" })
public class HomeController {
	@Autowired
	SessionFactory factory;
	
	@ModelAttribute("listProd")
	public List<Product> getLProd() {
		Session session = factory.getCurrentSession();
		String hql = "from Product";
		Query query = session.createQuery(hql);
		List<Product> listProd = query.list();
		return listProd;
	}
	
	@RequestMapping("index")
	public String index(ModelMap model) {
		model.addAttribute("prods", getLProd());
		return "home/index";
	}
	
	// view product
	@RequestMapping(value = { "products" })
	public String view_product(ModelMap model) {
		model.addAttribute("prods", getLProd());
		return "home/products";
	}

	// view product_detail
	@RequestMapping(value = { "detail" })
	public String view_product_detail(ModelMap model) {
		return "home/detail";
	}

//	@Autowired
//	Account user;
//	@ModelAttribute("user")
//	public Account getUser() {
//		return user;
//	}
}