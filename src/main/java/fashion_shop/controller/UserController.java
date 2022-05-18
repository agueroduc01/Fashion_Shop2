package fashion_shop.controller;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
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
	
	@ModelAttribute("listCus")
	public List<Account> getLcus() {
		Session session = factory.getCurrentSession();
		String hql = "from Account where Role = 2";
		Query query = session.createQuery(hql);
		List<Account> listcus = query.list();
		return listcus;
	}

	// Đăng Ký
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/register";
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		List<Account> l = getLUser();
		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu nhập đúng tài khoản");
		} else {
			for (Account a : l) {
				if (a.getUser_name().equalsIgnoreCase(user.getUser_name())) {
					errors.rejectValue("user_name", "user", "Tên tài khoản đã tồn tại!");
				}
			}
		}

		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu nhập đúng mật khẩu");
		} else {
			if (!user.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nhập trên 8 kí tự trong đó có chữ hoa thường và kí tự đặc biệt.");
			}
		}

		if (user.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "Yêu cầu không để trống tên tài khoản");
		}

		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (user.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email không được bỏ trống.");
		} else {
			Matcher matcher = pattern.matcher(user.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email không đúng định dạng");
			} else {
				for (Account a : l) {
					if (a.getEmail().equalsIgnoreCase(user.getEmail())) {
						errors.rejectValue("email", "user", "Tên email đã tồn tại");
					}
				}
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
				errors.rejectValue("phone", "user", "Yêu cầu nhập đúng số điện thoại");
		}

		if (user.getGender() == false) {
			errors.rejectValue("gender", "user", "Yêu cầu nhập đúng giới tính");
		}
		Role r = (Role) session.get(Role.class, 2);
		user.setRoles(r);

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
		Account acc = null;
		List<Account> listUser = getLUser();
		for (Account Account : listUser) {
			if (Account.getUser_name().equals(user.getUser_name())) {
				acc = Account;
				break;
			}
		}

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu không để trống tài khoản");
		}

		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu không để trống mật khẩu");
		}
		
		if (acc == null) {
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
		} else if (user.getPassword().equals(acc.getPassword())) {
			Thread.sleep(1000);
			httpSession.setAttribute("user", acc);
			boolean isAdmin = (boolean)acc.getRoles().equals((Object)1);
			if(isAdmin == true) {
				return "redirect:/admin/adminHome.html";
			} else {
				model.addAttribute("session", 1);
				return "redirect:/home/temp.htm";
			}
		} else
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
		return "user/login";
	}

	// Quên mật khẩu
	@RequestMapping(value = "forgotpassword", method = RequestMethod.GET)
	public String forgotpassword(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/forgotPassword";
	}

	@Autowired
	JavaMailSender mailer;

	// Gửi mail để lấy lại mật khẩu
	@RequestMapping(value = "forgotpassword", method = RequestMethod.POST)
	public String forgotpassword(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors) {
		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (user.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email không được bỏ trống.");
			return "user/forgotPassword";
		} else {
			Matcher matcher = pattern.matcher(user.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email không đúng định dạng.");
				return "user/forgotPassword";
			}
		}

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Account acc = (Account) session.get(Account.class, user.getEmail());
		Random rand = new Random();
		int rand_int1 = rand.nextInt(100000);

		String newPassword = Integer.toString(rand_int1);
		try {
			acc.setPassword(newPassword);
			String from = "n19dccn039@student.ptithcm.edu.vn";
			String to = acc.getEmail();
			String body = "Đây là mật khẩu mới của bạn: " + newPassword;
			String subject = "Quên mật khẩu";
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail);
			helper.setFrom(from, from);
			helper.setTo(to);
			helper.setReplyTo(from, from);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailer.send(mail);
			session.update(acc);
			t.commit();
			model.addAttribute("message", "Mật khẩu mới sẽ được gửi về mail của bạn!");

		} catch (Exception e) {
			model.addAttribute("message", "Không tìm thấy tài khoản nào với email này!");
			t.rollback();
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

	@RequestMapping(value = "changepassword/{username}", method = RequestMethod.POST)
	public String changepassword(ModelMap model, @ModelAttribute("user") Account user, HttpSession httpSession,
			BindingResult errors) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();

		Account acc = null;
		List<Account> listCus = getLcus();
		for (Account Account : listCus) {
			if (Account.getUser_name().equals(user.getUser_name())) {
				acc = Account;
				break;
			}
		}

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Yêu cầu không để trống tên tài khoản");
		}
		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Yêu cầu không để trống mật khẩu");
		}
		if (acc == null) {
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
		} else if (user.getPassword().equals(acc.getPassword())) {
			if (!user.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nhập trên 8 kí tự trong đó có chữ hoa thường và kí tự đặc biệt.");
			}
		} else
			model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");

		try {
			session.update(acc);
			t.commit();
			model.addAttribute("message", "Chỉnh sửa thành công");
			return "redirect:/user/dashboard.htm";

		} catch (Exception e) {
			model.addAttribute("message", "Chỉnh sửa thất bại !");
			t.rollback();
		} finally {
			session.close();
		}
		Account user1 = (Account) httpSession.getAttribute("admin");
		if (user1.getUser_name().equals(user.getUser_name()))
			httpSession.setAttribute("user", user);
		return "user/changePassword";
	}
}
