package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.deadbolt.Role;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name = "request_state", uniqueConstraints=@UniqueConstraint(columnNames={"request_state"}))
public class RequestState extends MyModel {

	public static final String ACTIVE   = "active";
	public static final String DISABLED = "disabled";
	public static final String CANCELED = "canceled";
	public static final String ACCEPTED = "accepted";
	public static final String PENDING  = "pending";
	public static final String CLOSED   = "closed";
	public static final String FEEDBACK = "feedback";
	
	@Required
	@MaxSize(100)
	@Unique(message="&{requestState.validation.unique.name}")
	@Column(name = "request_state", nullable = false)
	public String requestState;

	@OneToMany(mappedBy = "state")
	public Set<TravelRequest> travelRequests = new HashSet<TravelRequest>(0);

	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public RequestState() {
	}

	public RequestState(String name) {
		this.requestState = name;
	}

	@Override
	public String toString() {
		return requestState;
	}

	public static RequestState findByName(String state) {
		return find("byRequestState", state).first();
	}
}
