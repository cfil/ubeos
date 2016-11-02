package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "provider_rating")
public class ProviderRating extends MyModel {
	
	@Required
	@ManyToOne
	public Consumer consumer;
	
	@Required
	@ManyToOne
	public Provider provider;
	
	@Required
	@MaxSize(300)
	@Column(name = "description")
	public String description;
    
	@Required
	@Min(1)
	@Max(5)
	@Column(name = "value")
	public long value;
	
	@OneToOne
	public Proposal proposal;

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public ProviderRating(){
	}

	public static ProviderRating findByProposal(Proposal proposal) {
		return ProviderRating.find("byProposal", proposal).first();
	}

	public static List<ProviderRating> findByProvider(Provider provider) {
		return ProviderRating.find("byProvider", provider).fetch();
	}
	
	public static List<ProviderRating> findByCompany(Company company) {
		List<ProviderRating> ratings = new ArrayList<ProviderRating>();
		
		List<Provider> providers = Provider.findByCompany(company);
		for (Provider provider2 : providers) {
			List<ProviderRating> single = ProviderRating.find("byProvider", provider2).fetch();
			ratings.addAll(single);
		}
		
		return ratings;
	}
	
	public static double getProviderRating(Provider provider){
		List<ProviderRating> p_rates = new ArrayList<ProviderRating>();
		List<Provider> providers = Provider.findByCompany(provider.company);
		for (Provider p_tmp : providers) {
			List<ProviderRating> p_rates_tmp = ProviderRating.findByProvider(p_tmp);
			p_rates.addAll(p_rates_tmp);
		}
		
		int total_rates = p_rates.size();
		if(total_rates > 0){
			long total_sum = 0L;
			for (ProviderRating providerRating : p_rates) {
				total_sum += providerRating.value;
			}
			
			return total_sum / total_rates;
		} else {
			return 0;
		}
		
	}
	
}
