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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.OnDelete;

import models.deadbolt.RoleHolder;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.libs.Crypto;
import play.libs.Crypto.HashType;

@Entity
@Table(name = "ubeos_user", uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class User extends MyModel implements RoleHolder{

	@Required
	@MaxSize(value = 20)
	@Column(name = "first_name")
	public String firstName;

	@Required
	@MaxSize(value = 20)
	@Column(name = "last_name")
	public String lastName;
	
	@Email
	@Required
	@Column(name = "email")
	@Unique(message="&{validation.user.unique.email}")
	public String email;

	@Column(name = "birthdate")
	public Date birthDate;
	
	@Transient
	public int birth_day;
	
	@Transient
	public int birth_month;
	
	@Transient
	public int birth_year;
	
	@Column(name = "address")
	@MaxSize(value = 200)
	public String address;
	
	@Column(name = "city")
	@MaxSize(value = 50)
	public String city;
	
	@Column(name = "zipcode")
	public String zipcode;
	
	@ManyToOne
	public Country country;
	
	@Column(name = "lang")
	public String lang;
	
	@Transient
	public String password;

	@Transient
	@Equals(value="password", message="&{validation.passwords.mismatch}")
	public String passwordConfirm;

	@Column(name = "password")
	@Password
	public String hashPassword;

	@Column(name = "enable", nullable = false)
	public boolean enable = false;
	
	@Required
	@ManyToOne
	public UserRole userRole;
	
	@OneToOne
	public Avatar avatar;
	
	@Column(name = "phone")
	public Long phone;
	
	@Column(name = "blocked", nullable = false)
	public boolean blocked = false;
	
	@Column(name = "fb_id")
	public Long fb_id;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public User(){
	}

	public User(String first_name, String last_name, String email){
		this.firstName = first_name;
		this.lastName = last_name;
		this.email = email;
	}

	public String defaultDescription() {
		return firstName + " " + lastName;
	}

	public static User findByLogin(String login) {
		return User.find("byLogin", login).first();
	}

	public static User findByEmail(String email) {
		return User.find("byEmail", email).first();
	}

	public static String hashPassword(String password2) {
		return Crypto.passwordHash(password2, HashType.SHA512);
	}
	
	public static List<User> findByUserRole(UserRole userRole) {
		return User.find("byUserRole", userRole).fetch();
	}
	
	public boolean checkPassword(String password){
		// Check is password is right
		if(hashPassword == null) {
			return false;
		}
		
		String hashedPassword = hashPassword(password);
		return hashedPassword == null ? false: hashPassword.equals(hashedPassword);	
	}
	
	public List<String> getUserTypes() {
		List<String> userTypes = new ArrayList<String>();
		userTypes.add(UserRole.CONSUMER);
		userTypes.add(UserRole.PROVIDER);
		return userTypes;
	}
	
	// ********* Private Methods
	
	/**
	 * 
	 * @return randomly generated token
	 */
	public static String generateToken() {
		String token = UUID.randomUUID().toString();
		token = token.replace("-", "");
		Date date = Calendar.getInstance().getTime();
		return token + date.getTime();
	}

	@Override
	public List<UserRole> getRoles() {
		List<UserRole> userRoles = new ArrayList<UserRole>();
		userRoles.add(userRole);
		return userRoles;
	}

}
