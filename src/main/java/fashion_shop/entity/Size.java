package fashion_shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Size")
public class Size {
	@Id
	@Column(name = "nameSize")
	private String size;
	
	@ManyToOne
	@JoinColumn(name = "idProduct")
	private Product idProd;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Product getIdProd() {
		return idProd;
	}

	public void setIdProd(Product idProd) {
		this.idProd = idProd;
	}
}
