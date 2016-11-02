package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activation")
public class Activation extends MyModel {
    	
	@Column(name = "user")
	public Long user;
	
	@Column(name = "token")
	public String token;
	
	@Column(name = "enable", nullable = false)
	public boolean enable = true;
	
	@ManyToOne
	public ActivationMotive motive;
	
	@OneToOne
	public MessagesRec message;
	
	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public Activation() {
	}
	
	public Activation(Boolean enable, String token, ActivationMotive motive) {
		this.enable = enable;
		this.token = token;
		this.motive = motive;
	}
	
	public static Activation findByToken(String token){
		return Activation.find("byToken", token).first();
	}
	
}
