package software.amazonaws.example.product.entity;

import com.google.gson.annotations.SerializedName;

public class UserAddress  {
	
	private Long id;
	
	@SerializedName("street")
	private String street;

	
	@SerializedName("city")
	private String city; 
	
	@SerializedName("country")
	private String country; 
	
	@SerializedName("zip")
	private String zip;
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStreet() {
		return this.street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return this.country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZip() {
		return this.zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	@Override
	public String toString() {
		return "UserAddress [id=" + this.id + ", street=" + this.street + ", city=" + this.city + ", country=" + this.country + ", zip="
				+ this.zip + "]";
	}

	
}