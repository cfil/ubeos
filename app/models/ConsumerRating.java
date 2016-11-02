package models;

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

@Entity
@Table(name = "consumer_rating")
public class ConsumerRating extends MyModel {
	
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

	public ConsumerRating(){
	}

	public static ConsumerRating findByProposal(Proposal proposal) {
		return ConsumerRating.find("byProposal", proposal).first();
	}
	
	public static List<ConsumerRating> findByConsumer(Consumer consumer){
		return ConsumerRating.find("byConsumer", consumer).fetch();
	}

	public static double getConsumerRating(Consumer consumer){
		
		List<ConsumerRating> c_rates = ConsumerRating.findByConsumer(consumer);
		int total_rates = c_rates.size();
		
		if(total_rates > 0) {
			long total_sum = 0L;
			for (ConsumerRating consumerRating : c_rates) {
				total_sum += consumerRating.value;
			}
			
			
			return total_sum / total_rates;
		} else {
			return 0;
		}
		
	}
}
