package fashion_shop.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ProductCategory")
public class ProductCategory {
	@Id
	private String ID;
	private String Name;
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
}
