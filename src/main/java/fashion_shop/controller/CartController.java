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
@RequestMapping("/page/")
public class CartController {
	@Autowired
	SessionFactory factory;

	List<Cart> cartItem = new ArrayList<Cart>();

	public List<Cart> getcartItem() {
		return cartItem;
	}

	@RequestMapping({ "home" })
	public String index(HttpSession httpSession) {
		httpSession.setAttribute("cartItem", cartItem);
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));
		return "page/home";
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
		return "redirect:/";
	}

	public void restoreQuantityProduct(int id_product, int Quantity) {
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

	// into cart
	@RequestMapping("cart")
	public String cart(Model model, HttpSession httpSession) {
		httpSession.setAttribute("cartItem", cartItem);
		httpSession.setAttribute("totalprice", this.totalPrice(cartItem));
		httpSession.setAttribute("totalQuantity", this.totalQuantity(cartItem));
		return "page/cart";
	}

	@RequestMapping(value = "minusQuantity/{id_product}")
	public String minusQuantity(ModelMap model, HttpSession httpSession, @PathVariable("id_product") int id_product) {
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

		return "page/cart";
	}

	@RequestMapping(value = "plusQuantity/{id_product}")
	public String plusQuantity(ModelMap model, HttpSession httpSession, @PathVariable("id_product") int id_product) {
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
		return "page/cart";
	}

	@Autowired
	JavaMailSender mailer;

	@RequestMapping("checkout")
	public String checkout(ModelMap model, @ModelAttribute("user") Account user, HttpSession httpSession) {
		if (cartItem.size() == 0) {
			model.addAttribute("message", "KhÃ´ng cÃ³ sáº£n pháº©m nÃ o trong giá»� hÃ ng Ä‘á»ƒ thanh toÃ¡n");
			return "page/cart";
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
				model.addAttribute("message", "Checkout that bai");
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

			String from = "n17dccn157@student.ptithcm.edu.vn";
			String to = acc.getEmail();
			String body = "<html>" + "<body>\r\n" + "    <style>\r\n" + "        img{\r\n"
					+ "            max-width: 100%;\r\n" + "        }\r\n" + "        .container{\r\n"
					+ "            font-family: Arial,Helvetica,sans-serif;\r\n" + "            display: flex;\r\n"
					+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
					+ "            flex-direction: column;\r\n" + "            font-size: 12px;\r\n"
					+ "            color: #666666;\r\n" + "            font-weight: 400;\r\n"
					+ "            width: 500px;\r\n" + "            margin: 0 auto;\r\n" + "        }\r\n"
					+ "        \r\n" + "        .head{\r\n" + "            display: flex;\r\n"
					+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
					+ "            width: 100%;\r\n" + "            border-bottom: 3px solid red;\r\n" + "        }\r\n"
					+ "        .logo_mail{\r\n" + "            width: 150px;\r\n" + "        }\r\n"
					+ "        .notify h2{\r\n" + "            text-align: center;\r\n" + "        }\r\n"
					+ "        .info{\r\n" + "            border: 2px solid red;\r\n" + "            padding: 10px;\r\n"
					+ "        }\r\n" + "        .info_order{\r\n" + "            display: flex;\r\n"
					+ "            justify-content: center;\r\n" + "        }\r\n" + "        .img_product{\r\n"
					+ "            width: 100px;\r\n" + "            height: auto;\r\n"
					+ "            margin-right: 20px;\r\n" + "        }\r\n" + "        .btn{\r\n"
					+ "            text-align: center;\r\n" + "            display: block;\r\n"
					+ "            padding: 10px;\r\n" + "            text-decoration: none;\r\n"
					+ "            font-weight: bold;\r\n" + "            background-color: red;\r\n"
					+ "            width: 200px;\r\n" + "            color: white;\r\n"
					+ "            margin: 15px auto;\r\n" + "        }\r\n" + "        .foot{\r\n"
					+ "            text-align: center;\r\n" + "        }\r\n" + "    </style>\r\n" + "\r\n"
					+ "    <div class=\"container\">\r\n" + "        <div class=\"head\">\r\n"
					+ "            <a href=\"#\"><img src=\"./" + "resources/page/images/logobk1.png"
					+ "\" alt=\"\" class=\"logo_mail\"></a>\r\n" + "        </div>\r\n"
					+ "        <div class=\"content\">\r\n" + "            <div class=\"notify\">\r\n"
					+ "                <h2 style=\"color: red;\">THÃ”NG BÃ�O Ä�áº¶T HÃ€NG THÃ€NH CÃ”NG</h2>\r\n"
					+ "                <p>ChÃ o " + acc.getFullname() + ",</p>\r\n"
					+ "                <p>CÃ¡m Æ¡nu báº¡n Ä‘Ã£ mua sáº£n pháº©m táº¡i BakeryShop</p>\r\n"
					+ "                <br>\r\n"
					+ "                <p>Ä�Æ¡n hÃ ng cá»§a báº¡n Ä‘ang chá»� shop xÃ¡c nháº­n (trong vÃ²ng 24h)</p>\r\n"
					+ "                <p>ChÃºng tÃ´i sáº½ thÃ´ng tin tráº¡ng thÃ¡i Ä‘Æ¡n hÃ ng trong email tiáº¿p theo.</p>\r\n"
					+ "                <p>Báº¡n vui lÃ²ng kiá»ƒm tra email thÆ°á»�ng xuyÃªn nhÃ©.</p>\r\n"
					+ "            </div>\r\n" + "            <div class=\"info\">\r\n"
					+ "                <p class=\"id_order\"><strong>Ä�Æ¡n hÃ ng cá»§a báº¡n #</strong>   "
					+ order.getId_order() + " - NgÃ y Ä‘áº·t hÃ ng: <em>" + order.getOrder_date() + "</em></p>\r\n"
					+ "                <div class=\"info_order\">\r\n"
					+ "                    <div class=\"img_product\"> <img src=\"./"
					+ " resources/page/images/logobk1.png" + "\" alt=\"\"></div>\r\n"
					+ "                    <div class=\"detail_order\">\r\n"
					+ "                        <p><strong>Sáº£n pháº©m:</strong> " + sp + "</p>\r\n"
					+ "                        <p><strong>Tá»•ng thanh toÃ¡n:</strong> " + sum + "Ä‘</p>\r\n"
					+ "                        <p><strong>NgÆ°á»�i nháº­n:</strong> " + acc.getFullname() + " - "
					+ acc.getPhone() + " <br>\r\n" + "                            " + acc.getAddress() + "</p>\r\n"
					+ "                    </div>\r\n" + "\r\n" + "                </div>\r\n"
					+ "            </div>\r\n" + "            <a class=\"btn\" href=\"#\" >Chi tiáº¿t Ä‘Æ¡n hÃ ng</a>\r\n"
					+ "        </div>\r\n" + "        <div class=\"foot\">\r\n" + "            <span>\r\n"
					+ "Náº¿u báº¡n cÃ³ báº¥t cá»© cÃ¢u há»�i nÃ o, Ä‘á»«ng ngáº§n ngáº¡i liÃªn láº¡c vá»›i chÃºng tÃ´i táº¡i: n17dccn157@student.ptithcm.edu.vn <br>\r\n"
					+ "</body>\r\n" + "\r\n" + "</html>";
			String subject = "ChÃ o báº¡n  " + acc.getFullname();
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
		return "redirect:/other/successful.htm";
	}

	// buy something
	@RequestMapping("buyItem")
	public String buyItem(Model model, @RequestParam("idItem") int idItem, HttpSession httpSession) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Product prod = (Product) session.get(Product.class, idItem);
		Account user = (Account) httpSession.getAttribute("user");
		if (prod.getQuantity() == 0) {
			model.addAttribute("outOfStock", "Sáº£n pháº©m Ä‘Ã£ háº¿t hÃ ng");
			return "redirect:/page/home.htm";
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
		return "redirect:/page/home.htm";
	}

	// delete product
	@RequestMapping("deleteCartItem")
	public String deleteGh(Model model, HttpSession httpSession, @RequestParam("idItem") int idItem) {
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

		return "page/cart";
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

	@RequestMapping(value = { "change_password" }, method = RequestMethod.GET)
	public String change_password() {
		return "page/change_password";
	}

	@RequestMapping(value = { "change_password" }, method = RequestMethod.POST)
	public String change_password(ModelMap model, HttpServletRequest request,
			@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			@RequestParam("newPasswordAgian") String newPasswordAgian) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("user");

		if (!user.getPassword().equals(oldPassword)) {
			model.addAttribute("message1", "Máº­t kháº©u cÅ© khÃ´ng chÃ­nh xÃ¡c!");
		}
		if (oldPassword.length() == 0)
			model.addAttribute("message1", "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ rá»—ng");
		if (newPassword.length() == 0)
			model.addAttribute("message2", "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ rá»—ng");
		if (newPasswordAgian.length() == 0)
			model.addAttribute("message3", "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ rá»—ng");
		else if (!newPassword.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")
				|| !newPasswordAgian.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"))
			model.addAttribute("message", "Nháº­p trÃªn 8 kÃ­ tá»± trong Ä‘Ã³ cÃ³ chá»¯ Hoa thÆ°á»�ng vÃ  kÃ½ tá»± Ä‘áº·c biá»‡t");
		else if (!newPassword.equals(newPasswordAgian)) {
			model.addAttribute("message", "Máº­t kháº©u má»›i khÃ´ng trÃ¹ng nhau !");
		} else if (newPassword.equals(oldPassword)) {
			model.addAttribute("message", "Máº­t kháº©u má»›i khÃ´ng Ä‘Æ°á»£c trÃ¹ng vá»›i máº­t kháº©u cÅ© !");
		}

		else {

			try

			{
				user.setPassword(newPassword);
				session.update(user);
				t.commit();
				model.addAttribute("message", "Thay Ä‘á»•i máº­t kháº©u thÃ nh cÃ´ng");
				httpSession.setAttribute("user", user);
			} catch (

			Exception e) {
				model.addAttribute("message", "Thay Ä‘á»•i máº­t kháº©u tháº¥t báº¡i !");
				t.rollback();
			} finally

			{
				session.close();
			}

		}
		return "page/change_password";

	}

	@RequestMapping(value = { "info_user" }, method = RequestMethod.GET)
	public String info_user(ModelMap model, HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("user");
		httpSession.setAttribute("user", user);
		model.addAttribute("user", user);
		return "page/info_user";
	}

	@RequestMapping(value = { "info_user" }, method = RequestMethod.POST)
	public String info_user(ModelMap model, HttpServletRequest request, @ModelAttribute("user") Account user,
			BindingResult errors, @RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		HttpSession httpSession = request.getSession();
		List<Account> listUser = getLUser();
		if (user.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "TÃªn khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng!");
			errorss = true;
		}

		if (user.getAddress().trim().length() == 0) {
			errors.rejectValue("Address", "user", "Ä�á»‹a chá»‰ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng!");
			errorss = true;
		}

		// String regexNumber = "/^0[0-9]{8}$/";
		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (user.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "user", "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Æ°á»£c bá»� trá»‘ng.");
			errorss = true;
		} else {
			Matcher matcher1 = patternNumber.matcher(user.getPhone().trim());
			if (!matcher1.matches()) {
				errors.rejectValue("phone", "user", "YÃªu cáº§u nháº­p Ä‘Ãºng Sá»‘ Ä‘iá»‡n thoáº¡i");
				errorss = true;
			}
			if (listUser.size() == 1) {
			} else {
				for (Account a : listUser) {
					if (a.getPhone().equalsIgnoreCase(user.getPhone())
							&& !a.getUser_name().equals(user.getUser_name())) {
						errors.rejectValue("phone", "user", "Sá»‘ Ä‘iá»‡n thoáº¡i nÃ y Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng");
						errorss = true;
					}
				}
			}
		}

		if (user.getGender() == null) {
			errors.rejectValue("gender", "user", "YÃªu cáº§u nháº­p Ä‘Ãºng gender");
			errorss = true;
		}

		if (user.getImage() != null && !image.isEmpty()) {
			if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "user", "Vui lÃ²ng chá»�n file theo Ä‘Ãºng Ä‘á»‹nh dáº¡ng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					user.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "user", "Vui lÃ²ng chá»�n file theo Ä‘Ãºng Ä‘á»‹nh dáº¡ng!");
					errorss = true;
				}
			}
		} else if (user.getImage() == null && !image.isEmpty()) {
			if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "user", "Vui lÃ²ng chá»�n file theo Ä‘Ãºng Ä‘á»‹nh dáº¡ng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					user.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "user", "Vui lÃ²ng chá»�n file theo Ä‘Ãºng Ä‘á»‹nh dáº¡ng!");
					errorss = true;
				}
			}
		}

		if (errorss) {
			model.addAttribute("message", "YÃªu cáº§u nháº­p Ä‘áº§y Ä‘á»§ thÃ´ng tin !");
			return "page/info_user";
		}

		try

		{
			session.update(user);
			t.commit();
			model.addAttribute("message", "Sá»­a ThÃ´ng tin khÃ¡ch hÃ ng thÃ nh cÃ´ng");
			httpSession.setAttribute("user", user);
		} catch (

		Exception e) {
			model.addAttribute("message", "Sá»­a tháº¥t báº¡i !");
			t.rollback();
		} finally

		{
			session.close();
		}

		request.setAttribute("user", user);
		return "page/info_user";

	}

	@RequestMapping(value = "detail_product/{id_product}")
	public String detail_product(ModelMap model, @PathVariable("id_product") int id_product) {
		Session session = factory.openSession();
		Product prod = (Product) session.get(Product.class, id_product);
		model.addAttribute("detailProd", prod);
		return "page/detail_product";
	}

	@RequestMapping(value = "checkQuantityFromDetailProduct/{id_product}", params = "Quantity")
	public String checkQuantityFromDetailProduct(ModelMap model, @PathVariable("id_product") int id_product,
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

		return "redirect:/page/cart.htm";
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

		return "page/detail_order";
	}

	@RequestMapping(value = "purchase/{phone}")
	public String purchase(ModelMap model, @PathVariable("phone") String phone) {
		Session session = factory.openSession();
		String hql = "FROM Order o  where o.emails.phone = " + phone.substring(1) + "order by o.id_order ";
		Query query = session.createQuery(hql);
		List<Order> listOrder = query.list();
		model.addAttribute("Orders", listOrder);
		return "page/purchase";
	}

	@RequestMapping(value = "news", method = RequestMethod.GET)
	public String news(ModelMap model) {
		return "page/news";
	}

	@RequestMapping(value = "voucher", method = RequestMethod.GET)
	public String voucher(ModelMap model) {
		return "page/voucher";
	}

}
