package fashion_shop.controller;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
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
import org.springframework.web.multipart.MultipartFile;

import fashion_shop.entity.Account;
import fashion_shop.entity.Role;
import fashion_shop.DAO.accountDAO;

@Transactional
@Controller
@RequestMapping("/user/")
public class UserController {
	@Autowired
	SessionFactory factory;

	@Autowired
	accountDAO accountDAO;
	
	@Autowired
	ServletContext context;
	
	
	
	// Register GET
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/register";
	}
	
	// Register POST
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors
			,@RequestParam("passwordagain") String passwordagain,
			@RequestParam("photo")MultipartFile photo) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		List<Account> l = accountDAO.getLUser();
		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Y??u c???u nh???p kh??ng ????? tr???ng t??i kho???n");
		} else {
			for (Account a : l) {
				if (a.getUser_name().equalsIgnoreCase(user.getUser_name())) {
					errors.rejectValue("user_name", "user", "T??n t??i kho???n ???? t???n t???i!");
				}
			}
		}
		
//		if (photo.isEmpty()) {
//			errors.rejectValue("photo", "Kh??ng ????? file tr???ng!");
//		}
//		else {
		if (!photo.isEmpty()) {
			try {
				String photoPath = context.getRealPath("/files/" + photo.getOriginalFilename());
				photo.transferTo(new File(photoPath));
				user.setImage(photoPath);
//				model.addAttribute("photo", photoPath);
//				System.out.println(photo.getOriginalFilename());
			}
			catch (Exception e) {
				// TODO: handle exception
				errors.rejectValue("photo", "L???i l??u file!");
			}
		}
		
		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Y??u c???u kh??ng ????? tr???ng m???t kh???u");
		} else {
			if (!user.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
				errors.rejectValue("password", "user",
						"Nh???p tr??n 8 k?? t??? trong ???? c?? ch??? hoa, th?????ng v????k?? t??? ?????c bi???t.");
			}
		}

		if (user.getFullname().trim().length() == 0) {
			errors.rejectValue("fullname", "user", "Y??u c???u kh??ng ????? tr???ng h??? t??n");
		}

		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		if (user.getEmail().trim().length() == 0) {
			errors.rejectValue("email", "user", "Email kh??ng ???????c b??? tr???ng.");
		} else {
			Matcher matcher = pattern.matcher(user.getEmail().trim());
			if (!matcher.matches()) {
				errors.rejectValue("email", "user", "Email kh??ng ????ng ?????nh d???ng");
			}
		}
		
		// String regexNumber = "/^0[0-9]{8}$/";
		String regexNumber = "0\\d{9}";
		Pattern patternNumber = Pattern.compile(regexNumber);

		if (user.getPhone().trim().length() == 0) {
			errors.rejectValue("phone", "user", "S??? ??i???n tho???i kh??ng ???????c b??? tr???ng.");
		} else {
			Matcher matcher1 = patternNumber.matcher(user.getPhone().trim());
			if (!matcher1.matches())
				errors.rejectValue("phone", "user", "Y??u c???u nh???p ????ng ?????nh d???ng s??? ??i???n tho???i");
		}
		

		if (passwordagain.trim().length() == 0) {
			model.addAttribute("passwordagain", "Vui l??ng kh??ng ????? tr???ng m???t kh???u");
			return "user/register";
		}
		if (!passwordagain.equals(user.getPassword())) {
			model.addAttribute("passwordagain", "Vui l??ng nh???p tr??ng v???i m???t kh???u");
			return "user/register";
		}

		Role r = (Role) session.get(Role.class, 2);
		user.setrole(r);

		try {
			if (errors.hasErrors()) {
				model.addAttribute("messageRegister", "????ng k?? th???t b???i");
			} else {
				session.save(user);
				t.commit();
				model.addAttribute("messageRegister", "????ng k?? th??nh c??ng!");
			}
		} catch (Exception e) {
			t.rollback();
			model.addAttribute("messageRegister", "????ng k?? th???t b???i!");
		} finally {
			session.close();
		}
		return "user/register";
	}

	
	
	
	// Login
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
		List<Account> listUser = accountDAO.getLUser();
		for (int i = 0; i < listUser.size(); i++) {
			if (listUser.get(i).getUser_name().equals(user.getUser_name())) {
				acc = new Account();
				acc = listUser.get(i);
				temp = listUser.get(i);
				break;
			}
		}

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Y??u c???u kh??ng ????? tr???ng t??i kho???n");
		}

		if (user.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "user", "Y??u c???u kh??ng ????? tr???ng m???t kh???u");
		}
		
		if (acc.getUser_name() != temp.getUser_name()) {
			model.addAttribute("message", "T??n t??i kho???n ho???c m???t kh???u kh??ng ????ng!");
		} else if (user.getPassword().equals(acc.getPassword())) {
			Thread.sleep(1000);
			boolean isAdmin = (boolean) acc.getrole().getIdRole().equals((Object) 1);
			httpSession.setAttribute("acc", acc);
			if (isAdmin == true) {
				return "redirect:/admin/adminHome.htm";
			} else {
				String fromPage = (String) httpSession.getAttribute("fromPage");
				// session ????? l??u user l?? customer v?? quay l???i home
				model.addAttribute("session", httpSession.getAttribute("acc"));
				if (fromPage == "cart") {
					return "redirect:/cart/checkout.htm";
				} else {					
					return "redirect:/home/index.htm";
				}
			}
		} else
			model.addAttribute("message", "T??n t??i kho???n ho???c m???t kh???u kh??ng ????ng!");
		return "user/login";
	}

	@Autowired
	JavaMailSender mailer;

	// Qu??n m???t kh???u
	@RequestMapping(value = "forgotpassword", method = RequestMethod.GET)
	public String forgotpassword(ModelMap model) {
		model.addAttribute("user", new Account());
		return "user/forgotPassword";
	}

	// G???i mail ????? l???y l???i m???t kh???u
	@RequestMapping(value = "forgotpassword", method = RequestMethod.POST)
	public String forgotpassword(ModelMap model, @ModelAttribute("user") Account user, BindingResult errors,
			@RequestParam("user_name") String user_name) {

		if (user.getUser_name().trim().length() == 0) {
			errors.rejectValue("user_name", "user", "Y??u c???u kh??ng ????? tr???ng t??i kho???n");
			return "user/forgotPassword";
		}
		
		Account acc = new Account();
		List<Account> listUser = accountDAO.getLUser();
		for (int i = 0; i < listUser.size(); i++) {
			if (listUser.get(i).getUser_name().equals(user.getUser_name())) {
				acc = listUser.get(i);
				break;
			}
		}
		
		if (acc.getUser_name() == null) {
			errors.rejectValue("user_name", "user", "T??n t??i kho???n kh??ng ????ng!");
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
			String body = "????y l?? m???t kh???u m???i c???a b???n: " + newPassword;
			String subject = "Qu??n m???t kh???u";
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
           model.addAttribute("message", "G???i email th??nh c??ng !");
           t.commit();
		} catch (Exception e) {
			model.addAttribute("message", "G???i email th???t b???i !");
			t.rollback();
			throw new RuntimeException(e);
		} finally {
			session.close();
		}
		return "user/forgotPassword";
	}

	// ?????i m???t kh???u
	@RequestMapping(value = "changepassword", method = RequestMethod.GET)
	public String changepassword() {
		return "user/changePassword";
	}
	
	@RequestMapping(value = { "changepassword" },method = RequestMethod.POST)
	public String change_password(ModelMap model ,HttpServletRequest request,
			@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			@RequestParam("newPasswordAgain") String newPasswordAgain) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("acc");


		if (!user.getPassword().equals(oldPassword)) {
			model.addAttribute("message1", "M???t kh???u c?? kh??ng ch??nh x??c!");
		}
		if (oldPassword.length() == 0) {
			model.addAttribute("message1", "M???t kh???u kh??ng ???????c ????? tr???ng");			
		}
		
		if (newPassword.length() == 0) {
			model.addAttribute("message2", "M???t kh???u kh??ng ???????c ????? tr???ng");
		}

		if (newPasswordAgain.length() == 0) {
			model.addAttribute("message3", "M???t kh???u kh??ng ???????c ????? tr???ng");
		}
//			errors.rejectValue("newPasswordAgain", "user", "M???t kh???u kh??ng ???????c ????? tr???ng");
		else if (!newPassword.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")
				|| !newPasswordAgain.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"))
			model.addAttribute("message", "M???t kh???u m???i c???n tr??n 8 k?? t??? trong ???? c?? ch??? Hoa th?????ng v?? k?? t??? ?????c bi???t");
		else if (!newPassword.equals(newPasswordAgain)) {
			System.out.println("2");
			model.addAttribute("message", "M???t kh???u m???i kh??ng tr??ng nhau !");
		} else if (newPassword.equals(oldPassword)) {
			System.out.println("3");
			model.addAttribute("message", "M???t kh???u m???i kh??ng ???????c tr??ng v???i m???t kh???u c?? !");
		}
		
		else {
			try
			{
				user.setPassword(newPassword);
				session.update(user);
				t.commit();
				model.addAttribute("message", "Thay ?????i m???t kh???u th??nh c??ng!");
				httpSession.setAttribute("user", user);
			} catch (Exception e) {
				model.addAttribute("message", "Thay ?????i m???t kh???u th???t b???i!");
				t.rollback();
			} finally {
				session.close();
			}
		}
		return "user/changePassword";
	}
	
	//View c???a user home
	@RequestMapping(value = { "userHome" }, method = RequestMethod.GET)
	public String viewUserHome(ModelMap model, HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		Account user = (Account) httpSession.getAttribute("user");
		httpSession.setAttribute("user", user);
		model.addAttribute("user", user);
		return "user/userHome";
	}
	
	@RequestMapping(value="logout") 
	public String logOut(HttpServletRequest req) {
		HttpSession s = req.getSession();
		s.removeAttribute("acc");
		return "redirect:/home/index.htm";
	}
	
}