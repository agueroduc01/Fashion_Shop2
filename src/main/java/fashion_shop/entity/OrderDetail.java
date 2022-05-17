package fashion_shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

//import fashion_shop.entity;

@Entity
@Table(name = "OrderDetail")
public class OrderDetail {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private int id_detail;
	
	@Column(name = "Price")
	private float price;
	
	@Column(name = "Quantity")
	private int quantity;
	
	@ManyToOne
	@JoinColumn(name = "ID_order")
	private Order order;
	
	@ManyToOne 
	@JoinColumn(name = "ID_product")
	private Product product;
	
	public int getId_detail() {
		return id_detail;
	}

	public void setId_detail(int id_detail) {
		this.id_detail = id_detail;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
