package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.deadbolt.Role;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name = "user_role", uniqueConstraints=@UniqueConstraint(columnNames={"name"}))
public class UserRole extends MyModel implements Role{

	public final static String ADMIN = "admin";
	public static final String CONSUMER = "consumer";
	public static final String PROVIDER = "provider";
	
	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Required
	@MaxSize(100)
	@Unique(message="&{userrole.validation.unique.name}")
	@Column(name = "name", nullable = false)
	public String name;

	@OneToMany(mappedBy = "userRole")
	public Set<User> users = new HashSet<User>(0);

	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public UserRole() {
	}

	public UserRole(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getRoleName() {
		return name;
	}

	/**
	 * find UserRole by name
	 * 
	 * @param name
	 * @return
	 */
	public static UserRole findByName(String name) {
		return find("byName", name).first();
	}

//	public static UserRole findRole(User user) {		
//		return User.findByName(user.userType);
//	}

	public String roleName() {
		return "views.roles." + name;
	}

	public String description() {
		return "views.roles." + name + ".description";
	}
}
