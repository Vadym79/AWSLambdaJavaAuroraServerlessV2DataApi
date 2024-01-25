package software.amazonaws.example.product.entity;

import com.google.gson.annotations.SerializedName;

public class User {
	
	private Long id;
    
    @SerializedName("first_name")
	private String firstName;
    
    @SerializedName("last_name")
	private String lastName;
    
    @SerializedName("email")
	private String email;
    
    @SerializedName("address")
	private UserAddress userAddress;
	
	public UserAddress getUserAddress() {
		return this.userAddress;
	}
	public void setUserAddress(UserAddress userAddress) {
		this.userAddress = userAddress;
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", email=" + this.email
				+ ", userAddress=" + this.userAddress + "]";
	}
	
	
}