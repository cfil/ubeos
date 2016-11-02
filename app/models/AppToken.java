package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "app_token")
public class AppToken extends MyModel {
    	
	@Column(name = "user")
	public Long user;
	
	@Column(name = "a_token")
	public String a_token;
	
	@Column(name = "r_token")
	public String r_token;
	
	@Column(name = "enable", nullable = false)
	public boolean enable = true;
	
	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public AppToken() {
	}
	
	public AppToken(Long user, String a_token, String r_token) {
		super();
		this.user = user;
		this.a_token = a_token;
		this.r_token = r_token;
	}

	public static AppToken findBy_AToken(String token){
		return AppToken.find("byA_token", token).first();
	}
	
	public static AppToken findBy_RToken(String token){
		return AppToken.find("byR_token", token).first();
	}
	
	public static AppToken findByUser(User user){
		return AppToken.find("byUser", user.id).first();
	}
	
	public static List<AppToken> findByEnable(boolean state){
		return AppToken.find("byEnable", state).fetch();
	}
	
}
