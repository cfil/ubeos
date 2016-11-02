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
@Table(name = "proposal_state", uniqueConstraints=@UniqueConstraint(columnNames={"proposal_state"}))
public class ProposalState extends MyModel {

	public final static String ACTIVE = "active";
	public static final String DISABLED = "disabled";
	public static final String CLOSED = "closed";
	public static final String ACCEPTED = "accepted";
	public static final String REJECTED = "rejected";
	public static final String CANCELED = "canceled";
//	public static final String PENDING = "pending";
	public static final String FEEDBACK = "feedback";
	
	@Required
	@MaxSize(100)
	@Unique(message="&{proposalState.validation.unique.name}")
	@Column(name = "proposal_state", nullable = false)
	public String proposalState;

	@OneToMany(mappedBy = "state")
	public Set<Proposal> proposals = new HashSet<Proposal>(0);

	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public ProposalState() {
	}

	public ProposalState(String name) {
		this.proposalState = name;
	}

	@Override
	public String toString() {
		return proposalState;
	}

	public static ProposalState findByName(String state) {
		return find("byProposalState", state).first();
	}
}
