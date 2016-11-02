package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "reject_motive")
public class RejectMotive extends MyModel {

	public final static String ACCEPTED_OTHER = "motive.acc.other";
	public final static String DELETED = "motive.deleted";
	public final static String EXPIRED = "motive.expired";
	
	@Required
	@Column(name = "name")
	public String name;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public RejectMotive(){
	}

	public RejectMotive(String name){
		this.name = name;
	}

	public static RejectMotive findByName(String name) {
		return RejectMotive.find("byName", name).first();
	}

}
