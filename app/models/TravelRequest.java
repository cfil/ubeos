package models;

import helpers.RequestHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;

@Entity
@Table(name = "travel_request")
public class TravelRequest extends MyModel {

	public static final int MAX_DESCRIPTION_SIZE = 65535; 
	
	@ManyToOne
	public RequestState state;

	@Expose
	@Required
	@MaxSize(value = 150)
	@Column(name = "title")
	public String title;

	@Expose
	@Column(name = "description", length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String description;

	@Expose
	@Column(name = "description_two", length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionTwo;
	
	@Expose
	@Column(name = "description_three",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionThree;
	
	@Expose
	@Column(name = "description_four",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionFour;
	
	@Expose
	@Column(name = "description_people")
	@MaxSize(value = 1024)
	public String descriptionPeople;
	
	@Min(1)
	@Column(name = "min_budget")
	public long minBudget;

	@Expose
	@Required
	@Min(1)
	@Column(name = "max_budget")
	public long maxBudget;
	
	@Column(name = "ispublic")
	public boolean ispublic = false;
	
	@OneToMany(mappedBy="request", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Comment> comments = new HashSet<Comment>(0);
	
	@ManyToOne
	public Consumer consumer;
	
	@OneToMany(mappedBy = "request")
	public Set<Proposal> proposals = new HashSet<Proposal>(0);
	
	@Expose
	@Required
	@ManyToOne
	public Country country_from;
	
	@Expose
	@MaxSize(value = 50)
	@Column(name = "city_from")
	public String city_from;
		
	@Required
	@ManyToOne
	public Country country_to;
	
	@Expose
	@Required
	@MaxSize(value = 50)
	@Column(name = "city_to")
	public String city_to;
	
	@Transient
	public String date_from;
	
	@Expose
	@Required
	@Temporal(TemporalType.DATE)
	@Column(name = "datefrom")
	public Date dateFrom;
	
	@Transient
	public String date_to;
	
	@Expose
	@Required
	@Temporal(TemporalType.DATE)
	@Column(name = "dateto")
	public Date dateTo;
	
	@Expose
	@ManyToOne
	public Accommodation accommodation;
	
	@Expose
	@ManyToOne
	public Transport transport;
	
	@Expose
	@ManyToOne
	public Meal meal;
	
	@Expose
	@ManyToMany(fetch = FetchType.LAZY)
	public Set<Experience> experiences;
		
	@Column(name="enable")
	public boolean enable = true;
	
	@Expose
	@Column(name="flexible")
	public boolean flexible;
	
	@Expose
	@Required
	@Min(0)
	@Column(name="nights")
	public int nights;
	
	@Expose
	@Required
	@ManyToOne
	public Country country_location;
	
	@Expose
	@ManyToOne
	public Location location;
	
	@Expose
	@Required
	@Min(1)
	@Column(name="adults")
	public Long adults;
	
	@Expose
	@Required
	@Min(0)
	@Column(name="children")
	public Long children;
	
	@Expose
	@MaxSize(value = 50)
	@Column(name = "room_type")
	public String roomType;
	
	@Expose
	@Column(name="want_transportation")
	public boolean wantTransportation = false;
	
	@Expose
	@Column(name="want_accommodation")
	public boolean wantAccommodation = false;
	
	@Expose
	@Column(name="want_activities")
	public boolean wantActivities = false;

	@Expose
	@Required
	@Min(0)
	@Column(name="valid_to")
	public Long validTo;
	
	@Expose
	@Min(1)
	@Max(5)
	@Column(name="accommodation_stars")
	public int stars;
	
	@OneToMany(mappedBy = "travelRequest")
	public Set<MessagesRec> messages = new HashSet<MessagesRec>(0);
	
	@Expose
	@Column(name="image")
	public int image;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public TravelRequest(){
	}

	public TravelRequest(String title, String description){
		this.title = title;
		this.description = description;
		this.state = RequestState.findByName(RequestState.ACTIVE);
	}

	@Override
	public String toString() {
		return title;
	}

	public static List<TravelRequest> findByState(RequestState state) {
		return TravelRequest.find("byState",state).fetch();
	}
	
	public static List<TravelRequest> findActiveAndBloqued() {
		RequestState state = RequestState.findByName(RequestState.ACTIVE);
		
		List<TravelRequest> requests = new ArrayList<TravelRequest>();
		List<TravelRequest> requests_tmp = findByState(state);
		for (TravelRequest travelRequest : requests_tmp) {
			if( !RequestHelper.isValidForProposals(travelRequest) ){
				requests.add(travelRequest);
			}
		}
		return requests;
	}

	public static List<TravelRequest> findByStateAndConsumer(RequestState state, Consumer consumer){
		return TravelRequest.find("byStateAndConsumer", state, consumer).fetch();
	}
	
	public static boolean findForPendingTab(User user){
		
		Consumer consumer = Consumer.findByUser(user);
		
		List<TravelRequest> requests = TravelRequest.find("consumer_id = ? order by created_at desc",consumer).fetch();
		for (TravelRequest travelRequest : requests) {
			if(travelRequest.state.requestState.equals(RequestState.ACCEPTED) || travelRequest.state.requestState.equals(RequestState.PENDING) || travelRequest.state.requestState.equals(RequestState.FEEDBACK)){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean findForClosedTab(User user){
		
		Consumer consumer = Consumer.findByUser(user);
		
		List<TravelRequest> requests = TravelRequest.find("consumer_id = ? order by created_at desc",consumer).fetch();
		for (TravelRequest travelRequest : requests) {
			if(travelRequest.state.requestState.equals(RequestState.CLOSED) ){
				return true;
			}
		}
		
		return false;
	}

}