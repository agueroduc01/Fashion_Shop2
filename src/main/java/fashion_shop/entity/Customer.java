package fashion_shop.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "KHACHHANG")
public class Customer {
	@Id
	@Column
	private String email;
	
	@Column(name = "USER_NAME")
	private String user_name;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "HO")
	private String ho;
	@Column(name = "TEN")
	private String ten;
	
	@Column(name = "PHAI")
	private boolean gender;
	
	@Column(name = "SDT")
	private String phone;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "NGAYSINH")
	private Date birthday;
	
	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean getGender() {
		return this.gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}
	
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getFullname() {
		return this.ho + this.ten;
	}

	public void setFullname(String ho, String ten) {
		this.ho = ho;
		this.ten = ten;
	}
}
