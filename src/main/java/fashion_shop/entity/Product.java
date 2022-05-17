package fashion_shop.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Product")
public class Product {
	@Id
	@Column(name = "ID")
	private int id_product;

	@Column(name = "Name")
	private String name;
	
	private String IDCategory;
	private String Color;

	@Column(name = "Size")
	private float size;

	@Column(name = "Price")
	private float price;
	
	@Column(name = "Quantity")
	private int quantity;

	@Column(name = "Image")
	private String image;

	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Collection<OrderDetail> details;

	public int getId_product() {
		return id_product;
	}

	public void setId_product(int id_product) {
		this.id_product = id_product;
	}

	public Collection<OrderDetail> getDetail() {
		return details;
	}

	public void setDetail(Collection<OrderDetail> details) {
		this.details = details;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIDCategory() {
		return IDCategory;
	}

	public void setIDCategory(String IDCategory) {
		this.IDCategory = IDCategory;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		this.Color = color;
	}

	public Collection<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<OrderDetail> details) {
		this.details = details;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	

}
