package fashion_shop.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

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
import fashion_shop.entity.Role;

@Transactional
@Controller
@SessionAttributes(value = { "cus", "newAdmin", "p" })
@RequestMapping("/admin/")
public class AdminController {
	@Autowired
	SessionFactory factory;

	@ModelAttribute("listCus")
	public List<Account> getLcus() {
		Session session = factory.getCurrentSession();
		String hql = "from Account where Role = 2";
		Query query = session.createQuery(hql);
		List<Account> listcus = query.list();
		return listcus;
	}

	@ModelAttribute("listAdmin")
	public List<Account> getLAdmin() {
		Session session = factory.getCurrentSession();
		String hql = "from Account where Role = 1";
		Query query = session.createQuery(hql);
		List<Account> listAdmin = query.list();
		return listAdmin;
	}

	@ModelAttribute("listUser")
	public List<Account> getLUser() {
		Session session = factory.getCurrentSession();
		String hql = "from Account";
		Query query = session.createQuery(hql);
		List<Account> listUser = query.list();
		return listUser;
	}

	@ModelAttribute("listOrder")
	public List<Order> getLOrder() {
		Session session = factory.getCurrentSession();
		String hql = "FROM  Order order by id_order desc";
		Query query = session.createQuery(hql);
		List<Order> listOrder = query.list();
		return listOrder;
	}

	@RequestMapping(value = { "logoff" })
	public String logoff(HttpSession httpSession) {
		httpSession.removeAttribute("admin");
		return "redirect:/admin/login.htm";
	}

	@RequestMapping(value = { "dashboard" })
	public String dashboard(HttpServletRequest request, ModelMap model) {
		List<Order> listOrder = getLOrder();
		model.addAttribute("listOrder", listOrder);
		HttpSession httpSession = request.getSession();
		Account admin = (Account) httpSession.getAttribute("admin");
		httpSession.setAttribute("admin", admin);
		httpSession.setAttribute("totalCus", getLcus().size());
		httpSession.setAttribute("totalAd", getLAdmin().size());
		httpSession.setAttribute("totalOrder", getLOrder().size());
		return "admin/dashboard";
	}

	// view product
	@RequestMapping(value = { "view_product" })
	public String view_product(ModelMap model) {
		model.addAttribute("pros");
		return "admin/view_product";
	}

	@RequestMapping(value = { "view_customers" })
	public String view_customers(ModelMap model) {
		List<Account> listcus = getLcus();
		model.addAttribute("listCus", listcus);
		return "admin/view_customers";
	}

	@RequestMapping(value = { "view_admins" })
	public String view_admins(ModelMap model, HttpSession httpSession) {
		Session session = factory.getCurrentSession();
		Account admin = (Account) httpSession.getAttribute("admin");
		String hql = "from Account where Role = 1 and phone !=" + admin.getPhone().substring(1);
		Query query = session.createQuery(hql);
		List<Account> listAdmin = query.list();

		model.addAttribute("listAdmin", listAdmin);
		return "admin/view_admins";
	}

	@RequestMapping(value = { "view_orders" })
	public String view_orders(ModelMap model) {
		List<Order> listOrders = getLOrder();
		model.addAttribute("orders", listOrders);
		return "admin/view_orders";
	}

	// insert product
	@Autowired
	ServletContext context;

	public boolean FindFileByExtension(String fileName) {

		String path = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (path.equalsIgnoreCase("jpg") || path.equalsIgnoreCase("png") || path.equalsIgnoreCase("jpeg"))
			return true;
		return false;
	}

	@RequestMapping(value = { "insert_product" }, method = RequestMethod.GET)
	public String insert_product(ModelMap model, HttpSession httpSession) {
//		model.addAttribute("sizes", getSize());
		httpSession.removeAttribute("p");
		model.addAttribute("p", new Product());
		return "admin/insert_product";
	}

	@RequestMapping(value = "insert_product", method = RequestMethod.POST)
	public String insert_product(ModelMap model, @ModelAttribute("p") Product prod, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		if (prod.getName().trim().length() == 0) {
			errors.rejectValue("name", "p", "Tên không được để trống!");
			errorss = true;
		}

		if (prod.getPrice() <= 0) {
			errors.rejectValue("price", "p", "Giá phải lớn hơn 0!");
			errorss = true;
		}

		if (prod.getQuantity() <= 0) {
			errors.rejectValue("quantity", "p", "Số lượng phải lớn hơn 0!");
			errorss = true;
		}

		if (image.isEmpty()) {
			errors.rejectValue("image", "p", "Hình ảnh không được để trống!");
			errorss = true;
		} else if (!FindFileByExtension(image.getOriginalFilename())) {
			errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
			errorss = true;
		} else {
			try {
				Date date = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
				String dateFormat = simpleDateFormat.format(date);
				String imagePath = context
						.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
				image.transferTo(new File(imagePath));
				prod.setImage(dateFormat + image.getOriginalFilename());
			} catch (Exception e) {
				errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			}
		}

		if (!errorss)
			model.addAttribute("message", "Nhập chính xác");
		else {
			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
			return "admin/insert_product";
		}

		try {
//			prod.setStatus(true);
			session.save(prod);
			t.commit();
			model.addAttribute("message", "Thêm  thành công");
			return "redirect:/admin/view_product.htm";

		} catch (Exception e) {
			model.addAttribute("message", "Thêm thất bại !");
			t.rollback();
		} finally {
			session.close();
		}
		return "admin/insert_product";
	}

	// edit product

	@RequestMapping(value = { "edit_product/{id_product}" }, method = RequestMethod.GET)
	public String edit_product(ModelMap model, @PathVariable("id_product") int id_product, HttpSession httpSession) {
		Session session = factory.getCurrentSession();
		Product p = (Product) session.get(Product.class, id_product);
		model.addAttribute("p", p);
		httpSession.setAttribute("p", p);
		return "admin/edit_product";
	}

	@RequestMapping(value = "edit_product/{id_product}", method = RequestMethod.POST)
	public String edit_product(ModelMap model, @ModelAttribute("p") Product prod, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		if (prod.getName().trim().length() == 0) {
			errors.rejectValue("name", "p", "Tên không được để trống!");
			errorss = true;
		}

		if (prod.getPrice() == 0) {
			errors.rejectValue("price", "p", "Giá không được để trống!");
			errorss = true;
		}

		if (prod.getQuantity() < 0) {
			errors.rejectValue("quality", "p", "Số lượng phải lớn hơn không!");
			errorss = true;
		}
		if (prod.getImage() != null && !image.isEmpty()) {
			if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					prod.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
					errorss = true;
				}
			}
		}

		if (!errorss)
			model.addAttribute("message", "Nhập chính xác");
		else {
			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
			return "admin/edit_product";
		}

		try {
			session.update(prod);
			t.commit();
			model.addAttribute("message", "Sửa  thành công");
			return "redirect:/admin/view_product.htm";

		} catch (Exception e) {
			model.addAttribute("message", "Sửa thất bại !");
			t.rollback();
		} finally {
			session.close();
		}
		return "admin/edit_product";
	}

	// delete product
	@RequestMapping(value = { "delete_product/{id_product}" })
	public String delete_product(ModelMap model, @PathVariable("id_product") int id_product) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Product p = (Product) session.get(Product.class, id_product);
			session.delete(p);
			t.commit();
			model.addAttribute("message", "Xóa thành công");

		} catch (Exception ex) {
			model.addAttribute("message", "Không thể xóa sản phẩm này vì đã tồn tại trong hóa đơn khách hàng");
			t.rollback();
		} finally {
			session.close();
		}

		Session session1 = factory.getCurrentSession();
		String hql1 = "FROM Product";
		Query query = session1.createQuery(hql1);
		List<Product> list = query.list();
		List<Product> newList = new ArrayList<>();
		int countProduct = (list.size() < 28) ? list.size() : 28;
		for (int i = 0; i < countProduct; i++) {
			newList.add(list.get(i));
		}
		model.addAttribute("pros", newList);

		return "admin/view_product";
	}

	// delete product
	@RequestMapping(value = { "stop_product/{id_product}" })
	public String stop_product(ModelMap model, @PathVariable("id_product") int id_product) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
//		try {
//			Product p = (Product) session.get(Product.class, id_product);
//			if (p.isStatus()) {
//				p.setStatus(false);
//				model.addAttribute("message", "Sản phẩm đã chuyển sang trạng thái ngừng bán");
//			} else {
//				p.setStatus(true);
//				model.addAttribute("message", "Sản phẩm đã chuyển sang trạng thái bán");
//			}
//			session.update(p);
//			t.commit();
//		} catch (Exception ex) {
//			t.rollback();
//		} finally {
//			session.close();
//		}
		Session session1 = factory.getCurrentSession();
		String hql1 = "FROM Product";
		Query query = session1.createQuery(hql1);
		List<Product> list = query.list();
		List<Product> newList = new ArrayList<>();
		int countProduct = (list.size() < 28) ? list.size() : 28;
		for (int i = 0; i < countProduct; i++) {
			newList.add(list.get(i));
		}
		model.addAttribute("pros", newList);
		return "admin/view_product";
	}

	// insert cus
	@RequestMapping(value = { "insert_customer" }, method = RequestMethod.GET)
	public String insert_customer(ModelMap model, HttpSession httpSession) {
		httpSession.removeAttribute("cus");
		model.addAttribute("cus", new Account());
//		model.addAttribute("genders", genders());
		return "admin/insert_customer";
	}

	@RequestMapping(value = { "insert_customer" }, method = RequestMethod.POST)
	public String insert_customer(ModelMap model, @ModelAttribute("cus") Account cus, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		List<Account> l = getLUser();

		if (cus.getUser_name().trim().length() == 0) {
			errors.rejectValue("Address", "user", "Yêu cầu nhập đúng địa chỉ");
			errorss = true;
		}
		if (cus.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu nhập đúng tài khoản");
			errorss = true;
		} else {
			for (Account a : l) {
				if (a.getUser_name().equalsIgnoreCase(cus.getUser_name())) {
					errors.rejectValue("user_name", "user", "Tên tài khoản đã tồn tại");
					errorss = true;
				}
			}
		}

		if (cus.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu nhập đúng mật khẩu");
			errorss = true;
		} else {
			if (!cus.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nhập trên 8 kí tự trong đó có chữ Hoa thường và ký tự đặc biệt");
				errorss = true;
			}
		}

		if (cus.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "Họ tên không bỏ trống");
			errorss = true;
		}

		if (cus.getBirthday() == null) {
			errors.rejectValue("birthday", "user", "Ngày sinh không bỏ trống");
			errorss = true;
		}

		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (cus.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher = pattern.matcher(cus.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email không đúng định dạng");
				errorss = true;
			} else {
				for (Account a : l) {
					if (a.getEmail().equalsIgnoreCase(cus.getEmail())) {
						errors.rejectValue("email", "user", "Tên email đã tồn tại");
						errorss = true;
					}
				}
			}
		}

		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (cus.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "user", "Số điện thoại không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher1 = patternNumber.matcher(cus.getPhone().trim());
			if (!matcher1.matches()) {
				errors.rejectValue("phone", "user", "Yêu cầu nhập đúng Số điện thoại");
				errorss = true;
			}
			for (Account a : l) {
				if (a.getPhone().equalsIgnoreCase(cus.getPhone()) && !a.getUser_name().equals(cus.getUser_name())) {
					errors.rejectValue("phone", "user", "Số điện thoại này đã được sử dụng");
					errorss = true;
				}
			}
		}

		if (cus.getGender() == null) {
			errors.rejectValue("gender", "user", "Yêu cầu chọn đúng Giới tính");
			errorss = true;
		}
		if (image.isEmpty()) {
			errors.rejectValue("image", "cus", "Hình ảnh không được để trống!");
			errorss = true;
		} else if (!FindFileByExtension(image.getOriginalFilename())) {
			errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
			errorss = true;
		} else {
			try {
				Date date = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
				String dateFormat = simpleDateFormat.format(date);

				String imagePath = context
						.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
				image.transferTo(new File(imagePath));

				cus.setImage(dateFormat + image.getOriginalFilename());
			} catch (Exception e) {
				errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			}
		}

		Role r = (Role) session.get(Role.class, 2);
		cus.setRoles(r);

		try {
			if (errorss) {
				model.addAttribute("message", "Tạo tài khoản thất bại");
			} else {
				session.save(cus);
				t.commit();
				Session session2 = factory.openSession();
				Transaction t2 = session2.beginTransaction();
				try {

					String from = "n17dccn157@student.ptithcm.edu.vn";
					String to = cus.getEmail();
					String body = "<html><body>\r\n"
							+ "    <div class=\"container\" style=\"width: 700px; margin: auto; text-align: center;\">\r\n"
							+ "        <h2 class=\"title\" style=\"text-align: center; font-size: 30px; color: #666666; font-weight: 400;\">Ngọc Tuấn Bakery</h2>\r\n"
							+ "        <h3 class=\"sayHello\" style=\"text-align: center; font-size: 25px; color: #666666; font-weight: 400;\">\r\n"
							+ "            Chào mừng bạn đến với Ngọc Tuấn Bakery!</h3>\r\n"
							+ "            <p style=\"text-align: center; font-size: 18px; color: #666666; font-weight: 300;\">Chúc mừng bạn đã kích hoạt tài khoản khách hàng thành công. Lần mua hàng tiếp theo, hãy đăng nhập để việc thanh toán thuận tiện hơn.</p>\r\n"
							+ "        <a href=\"http://localhost:8080/BakeryShop/\" style=\"display: block; text-align: center; margin: 0 auto; text-decoration: none; padding: 15px; background-color: #1666A2; color: white; max-width: 200px; border-radius: 5px;\">\r\n"
							+ "            Đến cửa hàng của chúng tôi</a>\r\n" + "    </div>\r\n" + "</body>\r\n"
							+ "\r\n" + "</html>";
					String subject = "Chào bạn  " + cus.getFullname();
					MimeMessage mail = mailer.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mail);
					helper.setFrom(from, from);
					helper.setTo(to);
					helper.setReplyTo(from, from);
					helper.setSubject(subject);
					helper.setText(body, true);
					mailer.send(mail);
					t2.commit();
				} catch (Exception e) {
					model.addAttribute("abc", "Gửi mail thất bại");
					t2.rollback();
				} finally {
					session2.close();
				}
				model.addAttribute("message", "Tạo tài khoản thành công");
				return "redirect:/admin/view_customers.htm";
			}
		} catch (Exception e) {
			t.rollback();
			model.addAttribute("message", "Tạo tài khoản thất bại");
		} finally {
			session.close();
		}

		return "admin/insert_customer";

	}

	// edit cus

	@RequestMapping(value = { "edit_cus/{email}" }, method = RequestMethod.GET)
	public String edit_cus(ModelMap model, @PathVariable("email") String email, HttpSession httpSession) {
		Session session = factory.getCurrentSession();
		Account cus = (Account) session.get(Account.class, email);
		model.addAttribute("cus", cus);
//		model.addAttribute("genders", genders());
		httpSession.setAttribute("cus", cus);
		return "admin/edit_cus";
	}

	@RequestMapping(value = { "edit_cus/{email}" }, method = RequestMethod.POST)
	public String edit_cus(ModelMap model, @ModelAttribute("cus") Account cus, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		List<Account> l = getLUser();
		if (cus.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "cus", "Tên không được để trống!");
			errorss = true;
		}

		if (cus.getAddress().trim().length() == 0) {
			errors.rejectValue("Address", "cus", "Địa chỉ không được để trống!");
			errorss = true;
		}

		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (cus.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "cus", "Số điện thoại không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher1 = patternNumber.matcher(cus.getPhone().trim());
			if (!matcher1.matches()) {
				errors.rejectValue("phone", "cus", "Yêu cầu nhập đúng Số điện thoại");
				errorss = true;
			}
			for (Account a : l) {
				if (a.getPhone().equalsIgnoreCase(cus.getPhone()) && !a.getUser_name().equals(cus.getUser_name())) {
					errors.rejectValue("phone", "user", "Số điện thoại này đã được sử dụng");
					errorss = true;
				}
			}
		}
		if (!image.isEmpty() && cus.getImage() != null) {
			if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					cus.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
					errorss = true;
				}
			}
		}

		if (!errorss)
			model.addAttribute("message", "Nhập chính xác");
		else {
			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
			return "admin/edit_cus";
		}

		try {
			session.update(cus);
			t.commit();
			model.addAttribute("message", "Sửa  thành công");
			return "redirect:/admin/view_customers.htm";

		} catch (Exception e) {
			model.addAttribute("message", "Sửa thất bại !");
			t.rollback();
		} finally {
			session.close();
		}
		return "admin/edit_cus";
	}

	// delete cus
	@RequestMapping(value = { "delete_cus/{email}" })
	public String delete_cus(ModelMap model, @PathVariable("email") String email) {

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Account cus = (Account) session.get(Account.class, email);
			session.delete(cus);
			t.commit();
			model.addAttribute("message", "Xóa thành công");
		} catch (Exception ex) {
			model.addAttribute("message", "Khách hàng này đã mua hàng Không thể xóa!");
			t.rollback();
		} finally {
			session.close();
		}

		Session session1 = factory.getCurrentSession();
		String hql = "from Account where Role = 2";
		Query query = session1.createQuery(hql);
		List<Account> listcus = query.list();
		model.addAttribute("listcus", listcus);
		return "admin/view_customers";
	}

	public void restoreQuantityProduct(int id_order) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		String hql = "FROM OrderDetail d Where d.order.ID_order = " + id_order;
		Query query = session.createQuery(hql);
		List<OrderDetail> list = query.list();
		Product prod;
		for (OrderDetail o : list) {
			Session session1 = factory.openSession();
			Transaction t1 = session1.beginTransaction();
//			prod = (Product) session1.get(Product.class, o.getProduct().getidProduct());
//			try {
//				prod.setQuantity(prod.getQuantity() + o.getQuantity());
//				session1.update(prod);
//				t1.commit();
//			} catch (Exception e) {
//				t.rollback();
//			}
		}
		session.close();
	}

	@Autowired
	JavaMailSender mailer;

	@RequestMapping(value = { "confirm_order/{customer_email}/{id_order}/{status}" })
	public String confirm_order(ModelMap model, @PathVariable("customer_email") String customer_email,
			@PathVariable("id_order") int id_order, @PathVariable("status") int status) {

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			if (status == 0) {
				restoreQuantityProduct(id_order);
			}
			Order order = (Order) session.get(Order.class, id_order);
//			order.setStatus(status);
			session.update(order);
			t.commit();
		} catch (Exception ex) {
			t.rollback();
		} finally {
			session.close();
		}

		Session session2 = factory.openSession();
		Transaction t2 = session2.beginTransaction();
		Account acc = (Account) session2.get(Account.class, customer_email);
		OrderDetail detail = (OrderDetail) session2.get(OrderDetail.class, id_order);
		Order order = (Order) session2.get(Order.class, id_order);
		String hql = "FROM OrderDetail d where d.order.ID_order = " + id_order;
		Query query = session2.createQuery(hql);
		List<OrderDetail> listOrder = query.list();
		float sum = 0;
		String sp = "";
		for (OrderDetail o : listOrder) {
			sum += (o.getPrice() * o.getQuantity());
//			sp += o.getProduct().getName() + ", ";
		}

		try {

			String from = "n19dccn039@student.ptithcm.edu.vn";
			String to = customer_email;
			String body = "";
			if (status == 1) {
				body = "<html>" + "<body>\r\n" + "    <style>\r\n" + "        img{\r\n"
						+ "            max-width: 100%;\r\n" + "        }\r\n" + "        .container{\r\n"
						+ "            font-family: Arial,Helvetica,sans-serif;\r\n" + "            display: flex;\r\n"
						+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
						+ "            flex-direction: column;\r\n" + "            font-size: 12px;\r\n"
						+ "            color: #666666;\r\n" + "            font-weight: 400;\r\n"
						+ "            width: 500px;\r\n" + "            margin: 0 auto;\r\n" + "        }\r\n"
						+ "        \r\n" + "        .head{\r\n" + "            display: flex;\r\n"
						+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
						+ "            width: 100%;\r\n" + "            border-bottom: 3px solid red;\r\n"
						+ "        }\r\n" + "        .logo_mail{\r\n" + "            width: 150px;\r\n"
						+ "        }\r\n" + "        .notify h2{\r\n" + "            text-align: center;\r\n"
						+ "        }\r\n" + "        .info{\r\n" + "            border: 2px solid red;\r\n"
						+ "            padding: 10px;\r\n" + "        }\r\n" + "        .info_order{\r\n"
						+ "            display: flex;\r\n" + "            justify-content: center;\r\n"
						+ "        }\r\n" + "        .img_product{\r\n" + "            width: 100px;\r\n"
						+ "            height: auto;\r\n" + "            margin-right: 20px;\r\n" + "        }\r\n"
						+ "        .btn{\r\n" + "            text-align: center;\r\n"
						+ "            display: block;\r\n" + "            padding: 10px;\r\n"
						+ "            text-decoration: none;\r\n" + "            font-weight: bold;\r\n"
						+ "            background-color: red;\r\n" + "            width: 200px;\r\n"
						+ "            color: white;\r\n" + "            margin: 15px auto;\r\n" + "        }\r\n"
						+ "        .foot{\r\n" + "            text-align: center;\r\n" + "        }\r\n"
						+ "    </style>\r\n" + "\r\n" + "    <div class=\"container\">\r\n"
						+ "        <div class=\"head\">\r\n" + "            <a href=\"#\"><img src=\"./"
						+ "resources/page/images/logobk1.png" + "\" alt=\"\" class=\"logo_mail\"></a>\r\n"
						+ "        </div>\r\n" + "        <div class=\"content\">\r\n"
						+ "            <div class=\"notify\">\r\n"
						+ "                <h2 style=\"color: red;\">THÔNG BÁO XÁC NHẬN THÀNH CÔNG</h2>\r\n"
						+ "                <p>Chào " + acc.getFullname() + ",</p>\r\n"
						+ "                <p>Cám ơn bạn đã mua sản phẩm tại BakeryShop</p>\r\n"
						+ "                <br>\r\n"
						+ "                <p>Đơn hàng của bạn đang được vận chuyển trong ngày hôm nay</p>\r\n"
						+ "                <p>Chúng tôi sẽ thông tin trạng thái đơn hàng trong email tiếp theo.</p>\r\n"
						+ "                <p>Bạn vui lòng kiểm tra email thường xuyên nhé.</p>\r\n"
						+ "            </div>\r\n" + "            <div class=\"info\">\r\n"
						+ "                <p class=\"id_order\"><strong>Đơn hàng của bạn #</strong>   " + id_order
						+ " <em>" + order.getOrder_date() + "</em></p>\r\n"
						+ "                <div class=\"info_order\">\r\n"
						+ "                    <div class=\"img_product\"> <img src=\"./"
						+ " resources/page/images/logobk1.png" + "\" alt=\"\"></div>\r\n"
						+ "                    <div class=\"detail_order\">\r\n"
						+ "                        <p><strong>Sản phẩm:</strong> " + sp + "</p>\r\n"
						+ "                        <p><strong>Tổng thanh toán:</strong> " + sum + "đ</p>\r\n"
						+ "                        <p><strong>Người nhận:</strong> " + acc.getFullname() + " - "
						+ acc.getPhone() + " <br>\r\n" + "                            " + acc.getAddress() + "</p>\r\n"
						+ "                    </div>\r\n" + "\r\n" + "                </div>\r\n"
						+ "            </div>\r\n" + "            <a class=\"btn\" href=\"#\">Chi tiết đơn hàng</a>\r\n"
						+ "        </div>\r\n" + "        <div class=\"foot\">\r\n"
						+ "            <span>Nếu bạn có bất cứ câu hỏi nào, đừng ngần ngại liên lạc với chúng tôi tại: n17dccn157@student.ptithcm.edu.vn\r\n"
						+ "</body>\r\n" + "\r\n" + "</html>";

				// body = "Hóa đơn " + id_order + " của quý khách đã xác nhận thành công ";

			} else {
				body = "<html>" + "<body>\r\n" + "    <style>\r\n" + "        img{\r\n"
						+ "            max-width: 100%;\r\n" + "        }\r\n" + "        .container{\r\n"
						+ "            font-family: Arial,Helvetica,sans-serif;\r\n" + "            display: flex;\r\n"
						+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
						+ "            flex-direction: column;\r\n" + "            font-size: 12px;\r\n"
						+ "            color: #666666;\r\n" + "            font-weight: 400;\r\n"
						+ "            width: 500px;\r\n" + "            margin: 0 auto;\r\n" + "        }\r\n"
						+ "        \r\n" + "        .head{\r\n" + "            display: flex;\r\n"
						+ "            align-items: center;\r\n" + "            justify-content: center;\r\n"
						+ "            width: 100%;\r\n" + "            border-bottom: 3px solid red;\r\n"
						+ "        }\r\n" + "        .logo_mail{\r\n" + "            width: 150px;\r\n"
						+ "        }\r\n" + "        .notify h2{\r\n" + "            text-align: center;\r\n"
						+ "        }\r\n" + "        .info{\r\n" + "            border: 2px solid red;\r\n"
						+ "            padding: 10px;\r\n" + "        }\r\n" + "        .info_order{\r\n"
						+ "            display: flex;\r\n" + "            justify-content: center;\r\n"
						+ "        }\r\n" + "        .img_product{\r\n" + "            width: 100px;\r\n"
						+ "            height: auto;\r\n" + "            margin-right: 20px;\r\n" + "        }\r\n"
						+ "        .btn{\r\n" + "            text-align: center;\r\n"
						+ "            display: block;\r\n" + "            padding: 10px;\r\n"
						+ "            text-decoration: none;\r\n" + "            font-weight: bold;\r\n"
						+ "            background-color: red;\r\n" + "            width: 200px;\r\n"
						+ "            color: white;\r\n" + "            margin: 15px auto;\r\n" + "        }\r\n"
						+ "        .foot{\r\n" + "            text-align: center;\r\n" + "        }\r\n"
						+ "    </style>\r\n" + "\r\n" + "    <div class=\"container\">\r\n"
						+ "        <div class=\"head\">\r\n" + "            <a href=\"#\"><img src=\"./"
						+ "resources/page/images/logobk1.png" + "\" alt=\"\" class=\"logo_mail\"></a>\r\n"
						+ "        </div>\r\n" + "        <div class=\"content\">\r\n"
						+ "            <div class=\"notify\">\r\n"
						+ "                <h2 style=\"color: red;\">THÔNG BÁO HỦY ĐƠN HÀNG</h2>\r\n"
						+ "                <p>Chào " + acc.getFullname() + ",</p>\r\n"
						+ "                <p>Do sản phẩm tại BakeryShop đã hết hàng xin quý khách thông cảm đến mua hàng tại shop lần sau.</p>\r\n"
						+ "                <p>Xin cảm ơn và hẹn gặp lại</p>\r\n"
						+ "            </div>\r\n" + "            <div class=\"info\">\r\n"
						+ "                <p class=\"id_order\"><strong>Đơn hàng của bạn #</strong>   " + id_order
						+ " <em>" + order.getOrder_date() + "</em></p>\r\n"
						+ "                <div class=\"info_order\">\r\n"
						+ "                    <div class=\"img_product\"> <img src=\"./"
						+ " resources/page/images/logobk1.png" + "\" alt=\"\"></div>\r\n"
						+ "                    <div class=\"detail_order\">\r\n"
						+ "                        <p><strong>Sản phẩm:</strong> " + sp + "</p>\r\n"
						+ "                        <p><strong>Tổng thanh toán:</strong> " + sum + "đ</p>\r\n"
						+ "                        <p><strong>Người nhận:</strong> " + acc.getFullname() + " - "
						+ acc.getPhone() + " <br>\r\n" + "                            " + acc.getAddress() + "</p>\r\n"
						+ "                    </div>\r\n" + "\r\n" + "                </div>\r\n"
						+ "            </div>\r\n" + "            <a class=\"btn\" href=\"#\">Chi tiết đơn hàng</a>\r\n"
						+ "        </div>\r\n" + "        <div class=\"foot\">\r\n"
						+ "            <span>Nếu bạn có bất cứ câu hỏi nào, đừng ngần ngại liên lạc với chúng tôi tại: n17dccn157@student.ptithcm.edu.vn\r\n"
						+ "</body>\r\n" + "\r\n" + "</html>";
			}
			String subject = "BakeryShop đã nhận đơn hàng " + id_order;
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail);
			helper.setFrom(from, from);
			helper.setTo(to);
			helper.setReplyTo(from, from);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailer.send(mail);
			session2.update(acc);
			model.addAttribute("message", "Checkout thanh cong");
			t2.commit();

		} catch (Exception e) {
			model.addAttribute("message", "Checkout that bai");
			t2.rollback();
		} finally {
			session2.close();
		}

		return "redirect:/admin/view_orders.htm";
	}

	@RequestMapping(value = { "view_detail_order/{id_order}" })
	public String view_detail_order(ModelMap model, @PathVariable("id_order") int id_order) {

		Session session = factory.getCurrentSession();
		String hql = "FROM OrderDetail d where d.order.id_order = " + id_order;
		Query query = session.createQuery(hql);
		List<OrderDetail> listOrder = query.list();
		model.addAttribute("detailOrders", listOrder);
		float sum = 0;
		for (OrderDetail o : listOrder) {
			sum += (o.getPrice() * o.getQuantity());
		}

		model.addAttribute("sum", sum);

		return "admin/view_detail_order";
	}

	// insert admin
	@RequestMapping(value = { "insert_admin" }, method = RequestMethod.GET)
	public String insert_admin(ModelMap model, HttpSession httpSession) {
		httpSession.removeAttribute("newAdmin");
		model.addAttribute("newAdmin", new Account());
//		model.addAttribute("genders", genders());
		return "admin/insert_admin";
	}

	@RequestMapping(value = { "insert_admin" }, method = RequestMethod.POST)
	public String insert_admin(ModelMap model, @ModelAttribute("newAdmin") Account newAdmin, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		List<Account> l = getLUser();

		if (newAdmin.getUser_name().trim().length() == 0) {
			errors.rejectValue("Address", "user", "Yêu cầu nhập đúng địa chỉ");
			errorss = true;
		}
		if (newAdmin.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu nhập đúng tài khoản");
			errorss = true;
		} else {
			for (Account a : l) {
				if (a.getUser_name().equalsIgnoreCase(newAdmin.getUser_name())) {
					errors.rejectValue("user_name", "user", "Tên tài khoản đã tồn tại");
					errorss = true;
				}
			}
		}

		if (newAdmin.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu nhập đúng mật khẩu");
			errorss = true;
		} else {
			if (!newAdmin.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nhập trên 8 kí tự trong đó có chữ Hoa thường và ký tự đặc biệt");
				errorss = true;
			}
		}

		if (newAdmin.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "Họ tên không bỏ trống");
			errorss = true;
		}

		if (newAdmin.getBirthday() == null) {
			errors.rejectValue("birthday", "user", "Ngày sinh không bỏ trống");
			errorss = true;
		}

		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (newAdmin.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher = pattern.matcher(newAdmin.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email không đúng định dạng");
				errorss = true;
			} else {
				for (Account a : l) {
					if (a.getEmail().equalsIgnoreCase(newAdmin.getEmail())) {
						errors.rejectValue("email", "user", "Tên email đã tồn tại");
						errorss = true;
					}
				}
			}
		}

		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (newAdmin.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "user", "Số điện thoại không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher1 = patternNumber.matcher(newAdmin.getPhone().trim());
			if (!matcher1.matches()) {
				errors.rejectValue("phone", "user", "Yêu cầu nhập đúng Số điện thoại");
				errorss = true;
			}
			for (Account a : l) {
				if (a.getPhone().equalsIgnoreCase(newAdmin.getPhone())
						&& !a.getUser_name().equals(newAdmin.getUser_name())) {
					errors.rejectValue("phone", "user", "Số điện thoại này đã được sử dụng");
					errorss = true;
				}
			}
		}

		if (newAdmin.getGender() == null) {
			errors.rejectValue("gender", "user", "Yêu cầu chọn đúng Giới tính");
			errorss = true;
		}
		try {
			if (image.isEmpty()) {
				errors.rejectValue("image", "newAdmin", "Hình ảnh không được để trống!");
				errorss = true;
			} else if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "newAdmin", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					newAdmin.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "newAdmin", "Vui lòng chọn file theo đúng định dạng!");
					errorss = true;
				}
			}
		} catch (Exception e) {
			errors.rejectValue("image", "newAdmin", "Vui lòng chọn file theo đúng định dạng!");
		}

		Role r = (Role) session.get(Role.class, 1);
		newAdmin.setRoles(r);

		try {
			if (errorss) {
				model.addAttribute("message", "Tạo tài khoản thất bại");
			} else {
				session.save(newAdmin);
				t.commit();
				Session session2 = factory.openSession();
				Transaction t2 = session2.beginTransaction();
				try {

					String from = "n17dccn157@student.ptithcm.edu.vn";
					String to = newAdmin.getEmail();
					String body = "<html><body>\r\n"
							+ "    <div class=\"container\" style=\"width: 700px; margin: auto; text-align: center;\">\r\n"
							+ "        <h2 class=\"title\" style=\"text-align: center; font-size: 30px; color: #666666; font-weight: 400;\">Ngọc Tuấn Bakery</h2>\r\n"
							+ "        <h3 class=\"sayHello\" style=\"text-align: center; font-size: 25px; color: #666666; font-weight: 400;\">\r\n"
							+ "            Chào mừng bạn đến với Ngọc Tuấn Bakery!</h3>\r\n"
							+ "            <p style=\"text-align: center; font-size: 18px; color: #666666; font-weight: 300;\">Chúc mừng bạn đã kích hoạt tài khoản khách hàng thành công. Lần mua hàng tiếp theo, hãy đăng nhập để việc thanh toán thuận tiện hơn.</p>\r\n"
							+ "        <a href=\"http://localhost:8080/BakeryShop/\" style=\"display: block; text-align: center; margin: 0 auto; text-decoration: none; padding: 15px; background-color: #1666A2; color: white; max-width: 200px; border-radius: 5px;\">\r\n"
							+ "            Đến cửa hàng của chúng tôi</a>\r\n" + "    </div>\r\n" + "</body>\r\n"
							+ "\r\n" + "</html>";
					String subject = "Chào bạn  " + newAdmin.getFullname();
					MimeMessage mail = mailer.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mail);
					helper.setFrom(from, from);
					helper.setTo(to);
					helper.setReplyTo(from, from);
					helper.setSubject(subject);
					helper.setText(body, true);
					mailer.send(mail);
					t2.commit();
				} catch (Exception e) {
					model.addAttribute("abc", "Gửi mail thất bại");
					t2.rollback();
				} finally {
					session2.close();
				}
				model.addAttribute("message", "Tạo tài khoản thành công");
				return "redirect:/admin/view_admins.htm";
			}
		} catch (Exception e) {
			t.rollback();
			model.addAttribute("message", "Tạo tài khoản thất bại");
		} finally {
			session.close();
		}

		return "admin/insert_admin";

	}

	// edit admin

	@RequestMapping(value = { "edit_admin/{email}" }, method = RequestMethod.GET)
	public String edit_admin(ModelMap model, @PathVariable("email") String email, HttpSession httpSession) {
		Session session = factory.getCurrentSession();
		Account newAdmin = (Account) session.get(Account.class, email);
		model.addAttribute("newAdmin", newAdmin);
//		model.addAttribute("genders", genders());
		httpSession.setAttribute("newAdmin", newAdmin);
		return "admin/edit_admin";
	}

	@RequestMapping(value = { "edit_admin/{email}" }, method = RequestMethod.POST)
	public String edit_admin(ModelMap model, @ModelAttribute("newAdmin") Account newAdmin, BindingResult errors,
			@RequestParam("image") MultipartFile image, HttpSession httpSession) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		List<Account> l = getLUser();
		if (newAdmin.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "newAdmin", "Tên không được để trống!");
			errorss = true;
		}

		if (newAdmin.getAddress().trim().length() == 0) {
			errors.rejectValue("Address", "newAdmin", "Địa chỉ không được để trống!");
			errorss = true;
		}

		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (newAdmin.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "newAdmin", "Số điện thoại không được bỏ trống.");
			errorss = true;
		} else {
			Matcher matcher1 = patternNumber.matcher(newAdmin.getPhone().trim());
			if (!matcher1.matches()) {
				errors.rejectValue("phone", "newAdmin", "Yêu cầu nhập đúng Số điện thoại");
				errorss = true;
			}
			for (Account a : l) {
				if (a.getPhone().equalsIgnoreCase(newAdmin.getPhone())
						&& !a.getUser_name().equals(newAdmin.getUser_name())
						&& !a.getUser_name().equals(newAdmin.getUser_name())) {
					errors.rejectValue("phone", "user", "Số điện thoại này đã được sử dụng");
					errorss = true;
				}
			}
		}

		if (newAdmin.getImage() != null && !image.isEmpty()) {

			if (!FindFileByExtension(image.getOriginalFilename())) {
				errors.rejectValue("image", "newAdmin", "Vui lòng chọn file theo đúng định dạng!");
				errorss = true;
			} else {
				try {
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
					String dateFormat = simpleDateFormat.format(date);

					String imagePath = context
							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
					image.transferTo(new File(imagePath));

					newAdmin.setImage(dateFormat + image.getOriginalFilename());
				} catch (Exception e) {
					errors.rejectValue("image", "newAdmin", "Vui lòng chọn file theo đúng định dạng!");
					errorss = true;
				}
			}
		}

		if (!errorss)
			model.addAttribute("message", "Nhập chính xác");
		else {
			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
			return "admin/edit_admin";
		}

		try {
			session.update(newAdmin);
			t.commit();
			model.addAttribute("message", "Sửa  thành công");
			return "redirect:/admin/view_admins.htm";

		} catch (Exception e) {
			model.addAttribute("message", "Sửa thất bại !");
			t.rollback();
		} finally {
			session.close();
		}
		Account admin = (Account) httpSession.getAttribute("admin");
		if (admin.getUser_name().equals(newAdmin.getUser_name()))
			httpSession.setAttribute("admin", newAdmin);
		return "admin/edit_admin";
	}

	// delete admin
	@RequestMapping(value = { "delete_admin/{email}" })
	public String delete_admin(ModelMap model, HttpSession httpSession, @PathVariable("email") String email) {

		Account admin = (Account) httpSession.getAttribute("admin");
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Account newAdmin = (Account) session.get(Account.class, email);
			session.delete(newAdmin);
			t.commit();
			model.addAttribute("message", "Xóa thành công");
		} catch (Exception ex) {
			t.rollback();
		} finally {
			session.close();
		}

		Session session1 = factory.getCurrentSession();
		String hql = "from Account where Role = 1 where email !=" + admin;
		Query query = session1.createQuery(hql);
		List<Account> listadmin = query.list();
		model.addAttribute("listadmin", listadmin);
		return "redirect:/admin/view_admins.htm";
	}

}
