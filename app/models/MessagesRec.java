package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
@Table(name = "message_rec")
public class MessagesRec extends MyModel {

	public static final int CONS_2_PROV = 1;
	public static final int PROV_2_CONS = 2;
	
	@Column(name = "direction")
	@Required
	public int direction;
	
	@Column(name = "reply_to")
	public Long reply_to;
	
	@Required
	@ManyToOne
	public Provider provider;
	
	@Required
	@ManyToOne
	public Consumer consumer;
	
	@Column(name = "proposal_id")
	public Long proposal_id;
	
	@ManyToOne
	public TravelRequest travelRequest;
	
	@Column(name = "message")
	@MaxSize(value = 1024)
	public String message;
	
	// True if is new (not readed yet)
	@Column(name="is_new")
	public boolean isNew = true ;
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public MessagesRec(){
	}

	public MessagesRec(int direction, Provider provider, Consumer consumer,
			Long proposal_id, TravelRequest travelRequest, String message) {
		super();
		this.direction = direction;
		this.provider = provider;
		this.consumer = consumer;
		this.proposal_id = proposal_id;
		this.travelRequest = travelRequest;
		this.message = message;
	}
	
	public static List<MessagesRec> findByConsumerAndIncoming(Consumer consumer){
		return MessagesRec.find("byConsumerAndDirection", consumer, PROV_2_CONS).fetch();
	}
	
	public static List<MessagesRec> findByProviderAndIncoming(Provider provider){
		return MessagesRec.find("byProviderAndDirection", provider, CONS_2_PROV).fetch();
	}
	
	public static List<MessagesRec> findByConsumerAndSent(Consumer consumer){
		return MessagesRec.find("byConsumerAndDirection", consumer, CONS_2_PROV).fetch();
	}
	
	public static List<MessagesRec> findByProviderAndSent(Provider provider){
		return MessagesRec.find("byProviderAndDirection", provider, PROV_2_CONS).fetch();
	}

	public static List<MessagesRec> findByRequestAndConsumerReceiver(TravelRequest request,Consumer consumer ){
		return MessagesRec.find("byTravelRequestAndConsumerAndDirection", request,consumer,PROV_2_CONS).fetch();
	}
	
	public static List<MessagesRec> findByRequestAndProviderReceiver(TravelRequest request,Provider provider ){
		return MessagesRec.find("byTravelRequestAndProviderAndDirection", request,provider,CONS_2_PROV).fetch();
	}

	public static List<MessagesRec> findByProviderAndIncomingAndIsNew(Provider provider) {
		return MessagesRec.find("byProviderAndDirectionAndIsNew", provider, CONS_2_PROV, true).fetch();

	}

	public static List<MessagesRec> findByConsumerAndIncomingAndIsNew(Consumer consumer) {
		return MessagesRec.find("byConsumerAndDirectionAndIsNew", consumer, PROV_2_CONS, true).fetch();

	}
	
}
