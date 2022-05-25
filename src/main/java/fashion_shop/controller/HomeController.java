package fashion_shop.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fashion_shop.entity.Account;
import fashion_shop.entity.Product;
import fashion_shop.entity.ProductCategory;
import fashion_shop.service.DBService;
import fashion_shop.DAO.productDAO;
import fashion_shop.bean.CartItem;

@Controller
@RequestMapping(value = { "/home/", "/" })
public class HomeController {
	
	@Autowired
	SessionFactory factory;
	
	@Autowired
	productDAO productDAO1;
	
//	public List<Product> getLProd() {
//		Session session = factory.getCurrentSession();
//		String hql = "from Product";
//		Query query = session.createQuery(hql);
//		List<Product> listProd = query.list();
//		return listProd;
//	}
//	
//	public List<ProductCategory> getLCat() {
//		Session session = factory.getCurrentSession();
//		String hql = "from ProductCategory";
//		Query query = session.createQuery(hql);
//		List<ProductCategory> listCat = query.list();
//		return listCat;
//	}
	
	@RequestMapping("index")	
	public String index(ModelMap model) {
		model.addAttribute("prods", productDAO1.getLProd());
		return "home/index";
	}
	
	// view products
	@RequestMapping(value = { "products" })
	public String view_product(ModelMap model) {
		model.addAttribute("prods", productDAO1.getLProd());
		model.addAttribute("prodsSize", productDAO1.getLProd().size());
		model.addAttribute("catON", "false");
		return "home/products";
	}
	
	// view products by cat
	@RequestMapping(value = { "products/{idCategory}" })
	public String view_product(ModelMap model, @PathVariable("idCategory") String idCategory) {
		model.addAttribute("prods", productDAO1.getLProd());
		model.addAttribute("prodsSize",productDAO1.getLProd().size());
		model.addAttribute("listCat", productDAO1.getLCat());
		
		model.addAttribute("catON", "true");
		model.addAttribute("catID", idCategory);
		return "home/products";
	}	
	
	// view product_detail
	@RequestMapping(value = { "detail/{idProduct}" })
	public String view_product_detail(ModelMap model, @PathVariable("idProduct") String id, HttpSession session) {
		DBService db = new DBService(factory);
		Product product = db.getProductById(id);
		Account account = (Account) session.getAttribute("acc");

		CartItem cartItem = new CartItem();
		cartItem.setUserPhone(account.getPhone());
		cartItem.setIdProduct(product.getIdProduct());
		cartItem.setName(product.getName());
		cartItem.setColor(product.getColor());
		cartItem.setSize(product.getSize());
		cartItem.setPrice(product.getPrice());
		cartItem.setQuantity(product.getQuantity());
		cartItem.setImage(product.getImage());


		model.addAttribute("cartItem", cartItem);
		return "home/detail";
	}
	
	@RequestMapping(value = "detail_product/{id_product}")
	public String detail_product(ModelMap model, @PathVariable("id_product") String id_product) {
		Product prod = productDAO1.Product(id_product);
		model.addAttribute("detailProd", prod);
		return "home/detail";
	}
	
}