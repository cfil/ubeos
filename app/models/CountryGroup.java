package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;

@Entity
@Table(name = "country_group", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class CountryGroup extends MyModel {

	public final static String SUPPORTED = "supported";
	public final static String OTHER = "other";
	public final static String ALL = "all";
	
	@Required
	@Column(name = "name")
	public String name;
	
	@OneToMany(mappedBy="countryGroup", fetch=FetchType.LAZY)
	public Set<Country> countries = new HashSet<Country>(0);
	

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public CountryGroup(){
	}

	public CountryGroup(String name){
		this.name = name;
	}

	public static CountryGroup findByName(String name) {
		return CountryGroup.find("byName", name).first();
	}

}
