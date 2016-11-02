package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "source_restriction")
public class SourceRestriction extends MyModel {

	@ManyToMany(fetch=FetchType.LAZY)
	public Set<Location> locations;
	
//	@ElementCollection
//	@CollectionTable(name="locations_sources", joinColumns=@JoinColumn(name="source_restriction_id"))
//	@Column(name="locations" )
//	public Set<Long> locations = new HashSet<Long>();
	
	@OneToOne
	public Provider provider;
	
	@ManyToOne
	public CountryGroup countryGroup;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public SourceRestriction(){
	}
	
	public SourceRestriction(Set<Long> locations, Provider provider, CountryGroup countryGroup){
//		this.locations = locations;
		this.provider = provider;
		this.countryGroup = countryGroup;
	}

	public static SourceRestriction findByProvider(Provider provider) {
		return SourceRestriction.find("byProvider", provider).first();
	}
	
//	public static boolean contaisLocation(List<SourceRestriction> source_restrictions, Location location){
//		for (SourceRestriction sourceRestriction : source_restrictions) {
//			if (sourceRestriction.location.equals(location)){
//				return true;
//			}
//		}
//		return false;
//	}

}
