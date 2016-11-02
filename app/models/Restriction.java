package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;

@Entity
@Table(name = "restriction")
public class Restriction extends MyModel {

	@ManyToOne
	@Required
	public Country country;
	
	@Required
	@ManyToOne
	public Provider provider;
	

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Restriction(){
	}
	
	public Restriction(Country country, Provider provider){
		this.country = country;
		this.provider = provider;
	}

	public static List<Restriction> findByProvider(Provider provider) {
		return Restriction.find("byProvider", provider).fetch();
	}

}
