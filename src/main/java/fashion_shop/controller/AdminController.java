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
import fashion_shop.DAO.adminDAO;

@Transactional
@Controller
//@SessionAttributes(value = { "cus", "newAdmin", "p" })
@RequestMapping("/admin/")
public class AdminController {
	@Autowired
	SessionFactory factory;

	@RequestMapping("adminHome")
	public String viewAdmin() {
		return "admin/adminHome";
	}
	
	// view product
	@RequestMapping(value = { "adminProducts" }, method = RequestMethod.GET)
	public String adminProducts(ModelMap model) {
		model.addAttribute("pros");
		return "admin/adminProduct";
	}
	
	@RequestMapping(value = { "adminAccount" }, method = RequestMethod.GET)
	public String Customers(ModelMap model) {
//		List<Account> listcus = getLcus();
//		model.addAttribute("listCus", listcus);
		return "admin/adminAccount";
	}
	
	@RequestMapping(value = { "adminBill" }, method = RequestMethod.GET)
	public String adminBill(ModelMap model) {
//		List<Order> listOrders = getLOrder();
//		model.addAttribute("orders", listOrders);
		return "admin/adminBill";
	}
	
	@RequestMapping(value = { "adminBillInfo" }, method = RequestMethod.GET)
	public String adminBillInfo(ModelMap model) {
		return "admin/adminBillInfo";
	}
	
	@RequestMapping("adminAddProd")
	public String viewAdminAddProd() {
		return "admin/adminAddProd";
	}

////	@RequestMapping(value = { "adminHome" }, method = RequestMethod.GET)
////	public String adminHome(HttpServletRequest request, ModelMap model) {
//////		List<Order> listOrder = getLOrder();
//////		model.addAttribute("listOrder", listOrder);
//////		HttpSession httpSession = request.getSession();
//////		Account admin = (Account) httpSession.getAttribute("admin");
//////		httpSession.setAttribute("admin", admin);
//////		httpSession.setAttribute("totalCus", getLcus().size());
//////		httpSession.setAttribute("totalAd", getLAdmin().size());
//////		httpSession.setAttribute("totalOrder", getLOrder().size());
////		return "admin/adminHome";
////	}
	

//	// insert product
	@Autowired
	ServletContext context;

	public boolean FindFileByExtension(String fileName) {

		String path = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (path.equalsIgnoreCase("jpg") || path.equalsIgnoreCase("png") || path.equalsIgnoreCase("jpeg"))
			return true;
		return false;
	}
//
	@RequestMapping(value = { "insert_product" }, method = RequestMethod.GET)
	public String insert_product(ModelMap model) {
		model.addAttribute("p", new Product());
		return "admin/insert_product";
	}

	@RequestMapping(value = "insert_product", method = RequestMethod.POST)
	public String insert_product(ModelMap model, @ModelAttribute("p") Product prod, BindingResult errors,
			@RequestParam("image") MultipartFile image) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		boolean errorss = false;
		
		if (prod.getIdProduct().trim().length() == 0) {
			errors.rejectValue("idProduct", "p", "Tên không được để trống!");
		}
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
				String imagePath = context.getRealPath("resources/products/images/" + dateFormat + image.getOriginalFilename());
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
//
//	// edit product
//
//	@RequestMapping(value = { "edit_product/{id_product}" }, method = RequestMethod.GET)
//	public String edit_product(ModelMap model, @PathVariable("id_product") int id_product, HttpSession httpSession) {
//		Session session = factory.getCurrentSession();
//		Product p = (Product) session.get(Product.class, id_product);
//		model.addAttribute("p", p);
//		httpSession.setAttribute("p", p);
//		return "admin/edit_product";
//	}
//
//	@RequestMapping(value = "edit_product/{id_product}", method = RequestMethod.POST)
//	public String edit_product(ModelMap model, @ModelAttribute("p") Product prod, BindingResult errors,
//			@RequestParam("image") MultipartFile image) {
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
//		boolean errorss = false;
//		if (prod.getName().trim().length() == 0) {
//			errors.rejectValue("name", "p", "Tên không được để trống!");
//			errorss = true;
//		}
//
//		if (prod.getPrice() == 0) {
//			errors.rejectValue("price", "p", "Giá không được để trống!");
//			errorss = true;
//		}
//
//		if (prod.getQuantity() < 0) {
//			errors.rejectValue("quality", "p", "Số lượng phải lớn hơn không!");
//			errorss = true;
//		}
//		if (prod.getImage() != null && !image.isEmpty()) {
//			if (!FindFileByExtension(image.getOriginalFilename())) {
//				errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
//				errorss = true;
//			} else {
//				try {
//					Date date = new Date();
//					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
//					String dateFormat = simpleDateFormat.format(date);
//
//					String imagePath = context
//							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
//					image.transferTo(new File(imagePath));
//
//					prod.setImage(dateFormat + image.getOriginalFilename());
//				} catch (Exception e) {
//					errors.rejectValue("image", "p", "Vui lòng chọn file theo đúng định dạng!");
//					errorss = true;
//				}
//			}
//		}
//
//		if (!errorss)
//			model.addAttribute("message", "Nhập chính xác");
//		else {
//			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
//			return "admin/edit_product";
//		}
//
//		try {
//			session.update(prod);
//			t.commit();
//			model.addAttribute("message", "Sửa  thành công");
//			return "redirect:/admin/view_product.htm";
//
//		} catch (Exception e) {
//			model.addAttribute("message", "Sửa thất bại !");
//			t.rollback();
//		} finally {
//			session.close();
//		}
//		return "admin/edit_product";
//	}
//
//	// delete product
//	@RequestMapping(value = { "delete_product/{id_product}" })
//	public String delete_product(ModelMap model, @PathVariable("id_product") int id_product) {
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
//		try {
//			Product p = (Product) session.get(Product.class, id_product);
//			session.delete(p);
//			t.commit();
//			model.addAttribute("message", "Xóa thành công");
//
//		} catch (Exception ex) {
//			model.addAttribute("message", "Không thể xóa sản phẩm này vì đã tồn tại trong hóa đơn khách hàng");
//			t.rollback();
//		} finally {
//			session.close();
//		}
//
//		Session session1 = factory.getCurrentSession();
//		String hql1 = "FROM Product";
//		Query query = session1.createQuery(hql1);
//		List<Product> list = query.list();
//		List<Product> newList = new ArrayList<>();
//		int countProduct = (list.size() < 28) ? list.size() : 28;
//		for (int i = 0; i < countProduct; i++) {
//			newList.add(list.get(i));
//		}
//		model.addAttribute("pros", newList);
//
//		return "admin/view_product";
//	}
//
//	// delete product
//	@RequestMapping(value = { "stop_product/{id_product}" })
//	public String stop_product(ModelMap model, @PathVariable("id_product") int id_product) {
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
////		try {
////			Product p = (Product) session.get(Product.class, id_product);
////			if (p.isStatus()) {
////				p.setStatus(false);
////				model.addAttribute("message", "Sản phẩm đã chuyển sang trạng thái ngừng bán");
////			} else {
////				p.setStatus(true);
////				model.addAttribute("message", "Sản phẩm đã chuyển sang trạng thái bán");
////			}
////			session.update(p);
////			t.commit();
////		} catch (Exception ex) {
////			t.rollback();
////		} finally {
////			session.close();
////		}
//		Session session1 = factory.getCurrentSession();
//		String hql1 = "FROM Product";
//		Query query = session1.createQuery(hql1);
//		List<Product> list = query.list();
//		List<Product> newList = new ArrayList<>();
//		int countProduct = (list.size() < 28) ? list.size() : 28;
//		for (int i = 0; i < countProduct; i++) {
//			newList.add(list.get(i));
//		}
//		model.addAttribute("pros", newList);
//		return "admin/view_product";
//	}
//
//	// edit cus
//
//	@RequestMapping(value = { "edit_cus/{email}" }, method = RequestMethod.GET)
//	public String edit_cus(ModelMap model, @PathVariable("email") String email, HttpSession httpSession) {
//		Session session = factory.getCurrentSession();
//		Account cus = (Account) session.get(Account.class, email);
//		model.addAttribute("cus", cus);
////		model.addAttribute("genders", genders());
//		httpSession.setAttribute("cus", cus);
//		return "admin/edit_cus";
//	}
//
//	@RequestMapping(value = { "edit_cus/{email}" }, method = RequestMethod.POST)
//	public String edit_cus(ModelMap model, @ModelAttribute("cus") Account cus, BindingResult errors,
//			@RequestParam("image") MultipartFile image) {
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
//		boolean errorss = false;
//		List<Account> l = getLUser();
//		if (cus.getFullname().trim().length() == 0) {
//			errors.rejectValue("fullname", "cus", "Tên không được để trống!");
//			errorss = true;
//		}
//
//		if (cus.getAddress().trim().length() == 0) {
//			errors.rejectValue("Address", "cus", "Địa chỉ không được để trống!");
//			errorss = true;
//		}
//
//		String regexNumber = "0\\d{9}";
//		Pattern patternNumber = Pattern.compile(regexNumber);
//
//		if (cus.getPhone().trim().length() == 0) {
//			errors.rejectValue("phone", "cus", "Số điện thoại không được bỏ trống.");
//			errorss = true;
//		} else {
//			Matcher matcher1 = patternNumber.matcher(cus.getPhone().trim());
//			if (!matcher1.matches()) {
//				errors.rejectValue("phone", "cus", "Yêu cầu nhập đúng Số điện thoại");
//				errorss = true;
//			}
//			for (Account a : l) {
//				if (a.getPhone().equalsIgnoreCase(cus.getPhone()) && !a.getUser_name().equals(cus.getUser_name())) {
//					errors.rejectValue("phone", "user", "Số điện thoại này đã được sử dụng");
//					errorss = true;
//				}
//			}
//		}
//		if (!image.isEmpty() && cus.getImage() != null) {
//			if (!FindFileByExtension(image.getOriginalFilename())) {
//				errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
//				errorss = true;
//			} else {
//				try {
//					Date date = new Date();
//					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss_");
//					String dateFormat = simpleDateFormat.format(date);
//
//					String imagePath = context
//							.getRealPath("resources/page/images/" + dateFormat + image.getOriginalFilename());
//					image.transferTo(new File(imagePath));
//
//					cus.setImage(dateFormat + image.getOriginalFilename());
//				} catch (Exception e) {
//					errors.rejectValue("image", "cus", "Vui lòng chọn file theo đúng định dạng!");
//					errorss = true;
//				}
//			}
//		}
//
//		if (!errorss)
//			model.addAttribute("message", "Nhập chính xác");
//		else {
//			model.addAttribute("message", "Yêu cầu nhập đầy đủ thông tin !");
//			return "admin/edit_cus";
//		}
//
//		try {
//			session.update(cus);
//			t.commit();
//			model.addAttribute("message", "Sửa  thành công");
//			return "redirect:/admin/view_customers.htm";
//
//		} catch (Exception e) {
//			model.addAttribute("message", "Sửa thất bại !");
//			t.rollback();
//		} finally {
//			session.close();
//		}
//		return "admin/edit_cus";
//	}
//
//	// delete cus
//	@RequestMapping(value = { "delete_cus/{email}" })
//	public String delete_cus(ModelMap model, @PathVariable("email") String email) {
//
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
//		try {
//			Account cus = (Account) session.get(Account.class, email);
//			session.delete(cus);
//			t.commit();
//			model.addAttribute("message", "Xóa thành công");
//		} catch (Exception ex) {
//			model.addAttribute("message", "Khách hàng này đã mua hàng Không thể xóa!");
//			t.rollback();
//		} finally {
//			session.close();
//		}
//
//		Session session1 = factory.getCurrentSession();
//		String hql = "from Account where Role = 2";
//		Query query = session1.createQuery(hql);
//		List<Account> listcus = query.list();
//		model.addAttribute("listcus", listcus);
//		return "admin/view_customers";
//	}
//
//	public void restoreQuantityProduct(int id_order) {
//		Session session = factory.openSession();
//		Transaction t = session.beginTransaction();
//		String hql = "FROM OrderDetail d Where d.order.ID_order = " + id_order;
//		Query query = session.createQuery(hql);
//		List<OrderDetail> list = query.list();
//		Product prod;
//		for (OrderDetail o : list) {
//			Session session1 = factory.openSession();
//			Transaction t1 = session1.beginTransaction();
//			prod = (Product) session1.get(Product.class, o.getProd().getIdProduct());
//			try {
//				prod.setQuantity(prod.getQuantity() + o.getQuantity());
//				session1.update(prod);
//				t1.commit();
//			} catch (Exception e) {
//				t.rollback();
//			}
//		}
//		session.close();
//	}
}