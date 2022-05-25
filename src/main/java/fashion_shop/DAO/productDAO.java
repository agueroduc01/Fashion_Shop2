package fashion_shop.DAO;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fashion_shop.entity.Product;
import fashion_shop.entity.ProductCategory;

@Transactional
@Repository
public class productDAO {
	@Autowired
	SessionFactory factory;
	
	public List<Product> getLProd() {
		Session session = factory.getCurrentSession();
		String hql = "from Product";
		Query query = session.createQuery(hql);
		List<Product> listProd = query.list();
		return listProd;
	}
	
	public List<ProductCategory> getLCat() {
		Session session = factory.getCurrentSession();
		String hql = "from ProductCategory";
		Query query = session.createQuery(hql);
		List<ProductCategory> listCat = query.list();
		return listCat;
	}
	
	public Product Product(String idProduct) {
		Session session = factory.getCurrentSession();
		String hql = "FROM Product where id = 'idProduct'";
		Query query = session.createQuery(hql);
		query.setParameter("id", idProduct);
		Product pd = (Product) query.list().get(0);
		return pd;
	}
}
