package models;

import java.util.HashSet;
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
@Table(name = "country", uniqueConstraints={@UniqueConstraint(columnNames={"code"})})
public class Country extends MyModel {

	@Required
	@Column(name = "code")
	public String code;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;
	
	@OneToMany(mappedBy="country", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Location> locations = new HashSet<Location>(0);
	
	@ManyToOne
	public CountryGroup countryGroup;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Country(){
	}

	public Country(String code){
		this.code = code;
	}

	public static Country findByCode(String code) {
		return Country.find("byCode", code).first();
	}

}
