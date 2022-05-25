package fashion_shop.controller;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fashion_shop.entity.Account;
import fashion_shop.entity.Role;

@Transactional
@Controller
@RequestMapping("/user/")
public class UserController {
	@Autowired
	SessionFactory factory;

	@ModelAttribute("listUser")
	public List<Account> getLUser() {
		Session session = factory.getCurrentSession();
		String hql = "From Account";
		Query query = session.createQuery(hql);
		List<Account> listUser = query.list();
		return listUser;
	}

	// Đăng Ký
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/register";
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors
			,@RequestParam("passwordagain") String passwordagain
			) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		List<Account> l = getLUser();
		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu nhập không để trống tài khoản");
		} else {
			for (Account a : l) {
				if (a.getUser_name().equalsIgnoreCase(user.getUser_name())) {
					errors.rejectValue("user_name", "user", "Tên tài khoản đã tồn tại!");
				}
			}
		}
		
		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu không để trống mật khẩu");
		} else {
			if (!user.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nhập trên 8 kí tự trong đó có chữ hoa, thường và kí tự đặc biệt.");
			}
		}

		if (user.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "Yêu cầu không để trống họ tên");
		}

		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (user.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email không được bỏ trống.");
		} else {
			Matcher matcher = pattern.matcher(user.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email không đúng định dạng");
			}
		}
		
		// String regexNumber = "/^0[0-9]{8}$/";
		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (user.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "user", "Số điện thoại không được bỏ trống.");
		} else {
			Matcher matcher1 = patternNumber.matcher(user.getPhone().trim());
			if (!matcher1.matches())
				errors.rejectValue("phone", "user", "Yêu cầu nhập đúng định dạng số điện thoại");
		}
		

		if (passwordagain.trim().length() == 0) {
			model.addAttribute("passwordagain", "Vui lòng không để trống mật khẩu");
			return "user/register";
		}
		if (!passwordagain.equals(user.getPassword())) {
			model.addAttribute("passwordagain", "Vui lòng nhập trùng với mật khẩu");
			return "user/register";
		}

		Role r = (Role) session.get(Role.class, 2);
		user.setrole(r);

		try {
			if (errors.hasErrors()) {
				model.addAttribute("messageRegister", "Đăng ký thất bại");
			} else {
				session.save(user);
				t.commit();
				model.addAttribute("messageRegister", "Đăng ký thành công!");
			}
		} catch (Exception e) {
			t.rollback();
			model.addAttribute("messageRegister", "Đăng ký thất bại!");
		} finally {
			session.close();
		}
		return "user/register";
	}

	// Đăng Nhập
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/login";
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(ModelMap model, HttpSession httpSession, @ModelAttribute("user") Account user,
			BindingResult errors) throws InterruptedException {
		Account temp = new Account();
		Account acc = new Account();
		List<Account> listUser = getLUser();
		for (int i = 0; i < listUser.size(); i++) {
			if (listUser.get(i).getUser_name().equals(user.getUser_name())) {
				acc = new Account();
				acc = listUser.get(i);
				temp = listUser.get(i);
				break;
			}
		}

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu không để trống tài khoản");
		}

		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu không để trống mật khẩu");
		}
		
//		test tên tài khoản đã tồn tại trong db chưa nhờ vào hashcode
		
		System.out.println(acc.getPassword());
		if (acc.hashCode() != temp.hashCode()) {
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
		} else if (user.getPassword().equals(acc.getPassword())) {
			Thread.sleep(1000);
			// chưa vào được admin (getrole đang là role)
			boolean isAdmin = (boolean) acc.getrole().equals((Object) 1);
			httpSession.setAttribute("acc", acc);
			if (isAdmin == true) {
				return "redirect:/admin/adminHome.htm";
			} else {
				String fromPage = (String) httpSession.getAttribute("fromPage");
				
				// session để lưu user là customer và quay lại home
				model.addAttribute("session", httpSession.getAttribute("acc"));
				if (fromPage == "cart") {
					return "redirect:/cart/checkout.htm";
				} else {					
					return "redirect:/home/index.htm";
				}
			}
		} else
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
		return "user/login";
	}

	@Autowired
	JavaMailSender mailer;

	// Quên mật khẩu
	@RequestMapping(value = "forgotpassword", method = RequestMethod.GET)
	public String forgotpassword(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/forgotPassword";
	}

	// Gửi mail để lấy lại mật khẩu
	@RequestMapping(value = "forgotpassword", method = RequestMethod.POST)
	public String forgotpassword(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors,
			@RequestParam("user_name") String user_name) {

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu không để trống tài khoản");
			return "user/forgotPassword";
		}
		
		Account acc = new Account();
		List<Account> listUser = getLUser();
		for (int i = 0; i < listUser.size(); i++) {
			if (listUser.get(i).getUser_name().equals(user.getUser_name())) {
				acc = listUser.get(i);
				break;
			}
		}
		
		if (acc.getUser_name() == null) {
			errors.rejectValue("user_name", "user", "Tên tài khoản không đúng!");
			return "user/forgotPassword";
		}

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Random rand = new Random();
		int rand_int1 = rand.nextInt(100000);

		String newPassword = Integer.toString(rand_int1);
		try {
			acc.setPassword(newPassword);
			String from = "n19dccn039@student.ptithcm.edu.vn";
			String to = acc.getEmail();
			String body = "Đây là mật khẩu mới của bạn: " + newPassword;
			String subject = "Quên mật khẩu";
			try {
				MimeMessage mail= mailer.createMimeMessage();
				MimeMessageHelper heper= new MimeMessageHelper(mail,true,"UTF-8");
				heper.setFrom(from, from);
				heper.setTo(to);
				heper.setReplyTo(from);
				heper.setSubject(subject);
				heper.setText(body,true);
				mailer.send(mail);
			}catch(Exception ex) {
				throw new RuntimeException(ex);
			}
           model.addAttribute("message", "Gửi email thành công !");
           t.commit();
		} catch (Exception e) {
			model.addAttribute("message", "Gửi email thất bại !");
			t.rollback();
			throw new RuntimeException(e);
		} finally {
			session.close();
		}
		return "user/forgotPassword";
	}

	// Đổi mật khẩu
	@RequestMapping(value = "changepassword/{username}", method = RequestMethod.GET)
	public String changepassword(ModelMap model, @PathVariable("username") String username, HttpSession httpSession) {
		Session session = factory.getCurrentSession();
		Account user = (Account) session.get(Account.class, username);
		model.addAttribute("user", user);
		httpSession.setAttribute("user", user);
		return "user/changePassword";
	}
	
	@RequestMapping(value = { "changepassword/{acc}" }, method = RequestMethod.POST)
	public String change_password(ModelMap model, HttpServletRequest request,
			@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			@RequestParam("newPasswordAgain") String newPasswordAgain) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("acc");

		if (!user.getPassword().equals(oldPassword)) {
			model.addAttribute("message1", "Mật khẩu cũ không chính xác!");
			return "redicrect:/user/changepassword/{acc}.htm";
		}
		if (oldPassword.length() == 0)
			model.addAttribute("message1", "Mật khẩu không được để trống");
		if (newPassword == null)
			model.addAttribute("message2", "Mật khẩu không được để trống");
		if (newPasswordAgain == null)
			model.addAttribute("message3", "Mật khẩu không được để trống");
		else if (!newPassword.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")
				|| !newPasswordAgain.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"))
			model.addAttribute("message", "Nhập trên 8 kí tự trong đó có chữ Hoa thường và ký tự đặc biệt");
		else if (!newPassword.equals(newPasswordAgain)) {
			model.addAttribute("message", "Mật khẩu mới không trùng nhau !");
		} else if (newPassword.equals(oldPassword)) {
			model.addAttribute("message", "Mật khẩu mới không được trùng với mật khẩu cũ !");
		}

		else {
			try
			{
				user.setPassword(newPassword);
				session.update(user);
				t.commit();
				model.addAttribute("message", "Thay đổi mật khẩu thành công!");
				httpSession.setAttribute("user", user);
			} catch (

			Exception e) {
				model.addAttribute("message", "Thay đổi mật khẩu thất bại!");
				t.rollback();
			} finally {
				session.close();
			}
		}
		return "user/changePassword";
	}
	
	//View của user home
	@RequestMapping(value = { "userHome" }, method = RequestMethod.GET)
	public String viewUserHome(ModelMap model, HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("user");
		httpSession.setAttribute("user", user);
		model.addAttribute("user", user);
		return "user/userHome";
	}
	
	@RequestMapping(value = { "userHome" }, method = RequestMethod.POST)
	public String viewUserHome(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors) {
		
		return "user/userHome";
	}
	
	@RequestMapping(value="logout") 
	public String logOut(HttpServletRequest req) {
		HttpSession s = req.getSession();
		s.removeAttribute("acc");
		return "redirect:/home/index.htm";
	}
	
}