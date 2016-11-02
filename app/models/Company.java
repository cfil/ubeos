package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
@Table(name = "company" , uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Company extends MyModel {

	@MaxSize(value = 15)
	@Required
	@Column(name = "name")
	public String name;
	
	@OneToMany(mappedBy="company",  cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Provider> providers;
	
	// User ID!!!
	@Column(name="owner")
	public Long owner;
	
	@MaxSize(value = 50)
	@Column(name = "rnavt")
	public String rnavt;
	
	@MaxSize(value = 150)
	@Column(name = "address")
	public String address;
	
	@OneToMany(mappedBy="company", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Comment> comments = new HashSet<Comment>(0);
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Company(){
	}

	public Company(String companyName){
		this.name = companyName;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Company findByOwner(Long owner_id){
		return Company.find("byOwner", owner_id).first();
	}
	
	public static Company findByName(String name){
		return Company.find("byName", name).first();
	}

}
