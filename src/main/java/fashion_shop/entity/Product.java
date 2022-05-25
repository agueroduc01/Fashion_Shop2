package fashion_shop.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Product")
public class Product {
	@Id
	@Column(name = "ID")
	private String idProduct;

	@Column(name = "Name")
	private String name;
	
	@Column(name = "Color")
	private String Color;

	@Column(name = "Size")
	private String size;

	@Column(name = "Price")
	private float price;
	
	@Column(name = "Quantity")
	private Integer quantity;

	@Column(name = "Image")
	private String image;

//	@OneToMany(mappedBy = "prod", fetch = FetchType.EAGER)
//	private Collection<OrderDetail> orderDetails;
	
	@OneToMany(mappedBy = "idProd", fetch = FetchType.EAGER)
	private Collection<Size> sizes;
	
	@OneToMany(mappedBy = "idProduct", fetch = FetchType.EAGER)
	private Collection<Color> colors;
	
	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Collection<Cart> carts;
	
	@ManyToOne 
	@JoinColumn(name = "IDCategory")
	private ProductCategory ProdCategory;

	public String getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		Color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Collection<Size> getSizes() {
		return sizes;
	}

	public void setSizes(Collection<Size> sizes) {
		this.sizes = sizes;
	}

	public Collection<Color> getColors() {
		return colors;
	}

	public void setColors(Collection<Color> colors) {
		this.colors = colors;
	}

	public ProductCategory getProductCategory() {
		return ProdCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		ProdCategory = productCategory;
	}

//	public Collection<OrderDetail> getOrderDetails() {
//		return orderDetails;
//	}
//
//	public void setOrderDetails(Collection<OrderDetail> orderDetails) {
//		this.orderDetails = orderDetails;
//	}
}