package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;

@Entity
@Table(name = "location", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Location extends MyModel {

	@Required
	@Column(name = "name")
	public String name;
	
	@ManyToOne
	public Country country;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Location(){
	}

	public Location(String name){
		this.name = name;
	}

	public static List<Location> findByCountry(Country country) {
		return Location.find("byCountry", country).fetch();
	}

}
