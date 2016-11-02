package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "billing")
public class Billing extends MyModel {

//	@Required
//	@Column(name = "nif")
//	public Long nif;
	
	@Required
	@Column(name = "phone")
	public Long phone;
	
	@Required
	@MaxSize(value = 100)
	@Column(name = "name")
	public String name;

	@Column(name = "address")
	@MaxSize(value = 200)
	@Required
	public String address;
	
	@Column(name = "city")
	@MaxSize(value = 50)
	@Required
	public String city;
	
	@Required
	@MaxSize(value = 50)
	public String zipcode;
	
	@ManyToOne
	@Required
	public Country country;
	
	@OneToOne
	public Proposal proposal;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Billing(){
	}


	public static Billing findByProposal(Proposal proposal) {
		return Billing.find("byProposal", proposal).first();
	}

}
