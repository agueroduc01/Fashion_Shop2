package fashion_shop.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Role")
public class Role {
	@Id
	@Column(name = "ID")
	private int IdRole;
	
	@Column(name = "Name")
	private String nameRole;
	
	@OneToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	private Collection<Account> accounts;

	public int getIdRole() {
		return IdRole;
	}

	public void setIdRole(int idRole) {
		IdRole = idRole;
	}

	public String getNameRole() {
		return nameRole;
	}

	public void setNameRole(String nameRole) {
		this.nameRole = nameRole;
	}

	public Collection<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Collection<Account> accounts) {
		this.accounts = accounts;
	}
}
