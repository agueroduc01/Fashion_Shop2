package fashion_shop.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fashion_shop.entity.Employee;

@Transactional

@Controller

@SessionAttributes(value = { "newAdmin", "p" })

@RequestMapping("/admin/")
public class AdminController {

	@Autowired
	SessionFactory factory;

	@ModelAttribute("listAdmin")
	public List<Employee> getLAdmin() {
		Session session = factory.getCurrentSession();
		String hql = "from Employee";
		Query query = session.createQuery(hql);
		List<Employee> listAdmin = query.list();
		return listAdmin;
	}

	@ModelAttribute("listUser")
	public List<Employee> getLUser() {
		Session session = factory.getCurrentSession();
		String hql = "from Customer";
		Query query = session.createQuery(hql);
		List<Employee> listUser = query.list();
		return listUser;
	}
}
