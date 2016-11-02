package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "reject_stat")
public class RejectStat extends MyModel {

	@Column(name = "consumer_user_id")
	public Long consumer_UserId;
	
	@Column(name = "provider_user_id")
	public Long provider_UserId;
	
	@Column(name = "request_id")
	public Long request_id;
	
	@Column(name = "proposal_id")
	public Long proposal_id;

	@Column(name = "motive_id")
	public Long motive_id;
		
	@MaxSize(300)
	@Column(name = "motive_desc")
	public String motive_desc;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public RejectStat(){
	}

	public RejectStat(Long consumer_UserId, Long provider_UserId,
			Long request_id, Long proposal_id, Long motive_id,
			String motive_desc) {		
		this.consumer_UserId = consumer_UserId;
		this.provider_UserId = provider_UserId;
		this.request_id = request_id;
		this.proposal_id = proposal_id;
		this.motive_id = motive_id;
		this.motive_desc = motive_desc;
	}


	
}
