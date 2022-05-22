package fashion_shop.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import fashion_shop.bean.Cart;
import fashion_shop.entity.Account;
import fashion_shop.entity.Order;
import fashion_shop.entity.OrderDetail;
import fashion_shop.entity.Product;

@Controller
@RequestMapping("/cart/")
public class CartController {
	@Autowired
	SessionFactory factory;

	List<Cart> cartItem = new ArrayList<Cart>();

	public List<Cart> getcartItem() {
		return cartItem;
	}

	public float totalPrice(List<Cart> list) {
		float totalprice = 0;
		for (Cart item1 : list) {
			totalprice = totalprice + item1.getQuantity() * item1.getPriceItem();
		}
		return totalprice;
	}

	public int totalQuantity(List<Cart> list) {
		int totalQuantity = 0;
		for (Cart item1 : list) {
			totalQuantity += item1.getQuantity();
		}
		return totalQuantity;
	}

	public void restoreQuantityProduct(String id_product, int Quantity) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Product prod = (Product) session.get(Product.class, id_product);
		try {
			prod.setQuantity(prod.getQuantity() + Quantity);
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}
	}
	
	@RequestMapping(value = "logoff")
	public String logoff(ModelMap model, HttpSession httpSession) {

		if (cartItem != null) {

			for (Cart c : cartItem) {
				restoreQuantityProduct(c.getIdItem(), c.getQuantity());
			}
			httpSession.removeAttribute("cartItem");
			httpSession.removeAttribute("totalprice");
			httpSession.removeAttribute("totalQuantity");
			cartItem.clear();
		}
		httpSession.setAttribute("user", null);
		httpSession.removeAttribute("user");
		return "redirect:/home/index.htm";
	}
	
	@RequestMapping("orderComplete")
	public String orderComplete(Model model) {
		return "cart/orderComplete";
	}
	
	public List<Product> getLProd() {
		Session session = factory.getCurrentSession();
		String hql = "from Product";
		Query query = session.createQuery(hql);
		List<Product> listProd = query.list();
		return listProd;
	}

	// into cart
	@RequestMapping("cart/{idProduct}")
	public String cart(Model model, @PathVariable("idProduct") String idProduct) {
		Cart itemCart = null;
		List<Product> list = getLProd();
		for (int i = 0; i < list.size(); i++) {
			if (idProduct.equals(list.get(i))) {
				if (list.get(i).getQuantity() == 0) {
					model.addAttribute("messageSoldout", "Đã bán hết sản phẩm");
				} else {
					itemCart = new Cart();
					itemCart.setIdItem(list.get(i).getIdProduct());
					itemCart.setImage(list.get(i).getImage());
					itemCart.setNameItem(list.get(i).getIdProduct());
					itemCart.setPriceItem(list.get(i).getPrice());
					itemCart.setQuantity(1);
					break;
				}
			}
		}
		cartItem.add(itemCart);
		return "redirect:/cart/checkout.htm";
	}
	
	@RequestMapping(value = { "detail/{idProduct}" })
	public String view_product_detail(ModelMap model, @PathVariable("idProduct") String idProduct) {
		Session session = factory.getCurrentSession();
		String hql = "FROM Product where idProduct = " + idProduct;
		List<Product> list = session.createQuery(hql).list();
		model.addAttribute("product", list);
		model.addAttribute("prods", getLProd());
		return "home/detail";
	}

	@RequestMapping(value = "minusQuantity/{id_product}")
	public String minusQuantity(ModelMap model, HttpSession httpSession, @PathVariable("id_product") String id_product) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();

		Cart cart = null;
		for (Cart c : cartItem) {
			if (c.getIdItem() == id_product) {
				cart = c;
				break;
			}
		}
		Product prod = (Product) session.get(Product.class, id_product);
		if (cart.getQuantity() > 1) {
			cart.setQuantity(cart.getQuantity() - 1);
			prod.setQuantity(prod.getQuantity() + 1);
		}

		try {
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}

		httpSession.setAttribute("cartItem", cartItem);
		httpSession.setAttribute("totalprice", this.totalPrice(cartItem));
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));

		return "redirect:/cart/checkout.htm";
	}

	@RequestMapping(value = "plusQuantity/{id_product}")
	public String plusQuantity(ModelMap model, HttpSession httpSession, @PathVariable("id_product") String id_product) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Product prod = (Product) session.get(Product.class, id_product);
		boolean isExist = false;
		Cart cart = null;
		for (Cart item : cartItem) {
			if (item.getIdItem() == id_product) {
				isExist = true;
				cart = item;
				break;
			}
		}
		if (!isExist) {
			Cart item = new Cart();
			item.setIdItem(id_product);
			item.setImage(prod.getImage());
			item.setNameItem(prod.getName());
			item.setPriceItem(prod.getPrice());
			item.setQuantity(1);
			cartItem.add(item);
		}

		if (prod.getQuantity() > 0) {
			cart.setQuantity(cart.getQuantity() + 1);
			prod.setQuantity(prod.getQuantity() - 1);
		}

		try {
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}

		httpSession.setAttribute("cartItem", cartItem);
		httpSession.setAttribute("totalprice", this.totalPrice(cartItem));
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));
		return "redirect:/cart/checkout.htm";
	}

	@Autowired
	JavaMailSender mailer;

	@RequestMapping("checkout")
	public String checkout(ModelMap model, @ModelAttribute("user") Account user, HttpSession httpSession) {
		if (cartItem.size() == 0) {
			model.addAttribute("message", "Không có sản phầm trong giỏ hàng để thanh toán");
			return "cart/checkOut";
		}
		List<OrderDetail> listOrder = new ArrayList<OrderDetail>();
		Long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		Order order = new Order();
		order.setEmails(user);
		order.setOrder_date(date);
		// 1 la chap nhan
		// 2 dang cho xu ly
		// 0 huy
//		order.setStatus(2);
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();

		try {
			session.save(order);
			t.commit();
			model.addAttribute("message", "Checkout thanh cong");

		} catch (Exception e) {
			t.rollback();
			model.addAttribute("message", "Checkout that bai");
		} finally {
			session.close();
		}
		int invoice = 0;
		Transaction t1;
		for (Cart cart : cartItem) {
			Session session1 = factory.openSession();
			t1 = session1.beginTransaction();
			Product prod = (Product) session1.get(Product.class, cart.getIdItem());
			OrderDetail detail = new OrderDetail();
			detail.setOrder(order);
//			detail.setProduct(prod);
			detail.setPrice(cart.getQuantity() * cart.getPriceItem());
			detail.setQuantity(cart.getQuantity());

			try {
				session1.save(detail);
				session1.save(prod);
				t1.commit();
				invoice = detail.getId_detail();
				listOrder.add(detail);
			} catch (Exception e) {
				t1.rollback();
				model.addAttribute("message", "Checkout thất bại");
			} finally {
				session1.close();
			}
		}

		cartItem.clear();
		httpSession.removeAttribute("cartItem");
		httpSession.removeAttribute("totalprice");
		httpSession.removeAttribute("totalQuantity");
		String sp = "";
		float sum = 0;
		for (int i = 0; i < listOrder.size(); i++) {
//			sp += listOrder.get(i).getProduct().getName() + ", ";
			sum += listOrder.get(i).getQuantity() * listOrder.get(i).getPrice();
		}

		Session session2 = factory.openSession();
		Transaction t2 = session2.beginTransaction();
		Account acc = (Account) session2.get(Account.class, user.getEmail());
		try {

			String from = "n19dccn039@student.ptithcm.edu.vn";
			String to = acc.getEmail();
			String body = "";
			String subject = "Chào bạn " + acc.getFullname();
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail);
			helper.setFrom(from, from);
			helper.setTo(to);
			helper.setReplyTo(from, from);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailer.send(mail);
			session2.update(acc);
			model.addAttribute("message", "Checkout successful");
			t2.commit();

		} catch (Exception e) {
			model.addAttribute("message", "Checkout fail");
			t2.rollback();
		} finally {
			session2.close();
		}
		model.addAttribute("invoice", order.getId_order());
		return "redirect:/cart/orderComplete.htm";
	}

	// buy something
	@RequestMapping("buyItem")
	public String buyItem(Model model, @RequestParam("idItem") String idItem, HttpSession httpSession) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Product prod = (Product) session.get(Product.class, idItem);
		Account user = (Account) httpSession.getAttribute("user");
		if (prod.getQuantity() == 0) {
			model.addAttribute("outOfStock", "Sản phẩm đã hết hàng");
			return "redirect:/cart/checkout.htm";
		}
		boolean isExist = false;
		for (Cart item : cartItem) {
			if (item.getIdItem() == idItem) {
				item.setQuantity(item.getQuantity() + 1);
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			Cart item = new Cart();
			item.setIdItem(idItem);
			item.setImage(prod.getImage());
			item.setNameItem(prod.getName());
			item.setPriceItem(prod.getPrice());
			item.setQuantity(1);

			cartItem.add(item);
		}

		prod.setQuantity(prod.getQuantity() - 1);

		try {
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}
		httpSession.setAttribute("cartItem", cartItem);
		httpSession.setAttribute("totalprice", this.totalPrice(cartItem));
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));
		session = factory.openSession();
		Account cus = (Account) session.get(Account.class, user.getEmail());
		httpSession.setAttribute("user", cus);
		session.close();
		return "redirect:/cart/orderComplete.htm";
	}

	// delete product
	@RequestMapping("deleteCartItem")
	public String deleteGh(Model model, HttpSession httpSession, @RequestParam("idItem") String idItem) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		int index = 0;
		Product prod = (Product) session.get(Product.class, idItem);
		for (int i = 0; i < cartItem.size(); i++) {
			if (cartItem.get(i).getIdItem() == idItem) {
				prod.setQuantity(prod.getQuantity() + cartItem.get(i).getQuantity());
				index = i;
				break;
			}
		}
		try {
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}
		cartItem.remove(index);
		httpSession.setAttribute("totalprice", this.totalPrice(cartItem));
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));
		httpSession.setAttribute("cartItem", cartItem);

		return "cart/checkOut";
	}

	@Autowired
	ServletContext context;

	public boolean FindFileByExtension(String fileName) {

		String path = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (path.equalsIgnoreCase("jpg") || path.equalsIgnoreCase("png") || path.equalsIgnoreCase("jpeg"))
			return true;
		return false;
	}

	@ModelAttribute("listUser")
	public List<Account> getLUser() {
		Session session = factory.openSession();
		String hql = "from Account";
		Query query = session.createQuery(hql);
		List<Account> listUser = query.list();
		return listUser;
	}


	@RequestMapping(value = "checkQuantityFromDetailProduct/{id_product}", params = "Quantity")
	public String checkQuantityFromDetailProduct(ModelMap model, @PathVariable("id_product") String id_product,
			@RequestParam("Quantity") int Quantity) {

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Product prod = (Product) session.get(Product.class, id_product);
		Cart c = null;
		boolean isExist = false;
		for (Cart item : cartItem) {
			if (item.getIdItem() == id_product) {
				c = item;
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			c = new Cart();
			c.setIdItem(id_product);
			c.setImage(prod.getImage());
			c.setNameItem(prod.getName());
			c.setPriceItem(prod.getPrice());
			c.setQuantity(Quantity);
			prod.setQuantity(prod.getQuantity() - Quantity);
			cartItem.add(c);
		} else if (prod.getQuantity() > 0) {
			c.setQuantity(c.getQuantity() + Quantity);
			prod.setQuantity(prod.getQuantity() - Quantity);
		}
		try {
			session.update(prod);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			session.close();
		}

		return "redirect:/cart/checkOut.htm";
	}

	@RequestMapping(value = "detail_order/{id_order}")
	public String detail_order(ModelMap model, @PathVariable("id_order") int id_order) {
		Session session = factory.openSession();

		String hql = "FROM OrderDetail d where d.order.id_order = " + id_order;
		Query query = session.createQuery(hql);
		List<OrderDetail> listOrder = query.list();
		model.addAttribute("Orders", listOrder);
		float sum = 0;
		for (OrderDetail o : listOrder) {
			sum += (o.getPrice() * o.getQuantity());
		}

		model.addAttribute("sum", sum);

		return "redirect:/cart/checkOut.htm";
	}
}
