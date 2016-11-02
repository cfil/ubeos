package models;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Blob;
import play.libs.Crypto;
import play.libs.Crypto.HashType;

@Entity
@Table(name = "avatar")
public class Avatar extends MyModel {

	@OneToOne
	public User user;
		
	@Required
	@Column(name = "avatar")
	public Blob avatar;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Avatar(){
	}

	public Avatar(User user, Blob avatar){
		this.avatar = avatar;
		this.user = user;
	}
	
	public static Avatar findByUser(User user){
		return Avatar.find("byUser", user).first();
	}

}
