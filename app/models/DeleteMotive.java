package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "delete_motive")
public class DeleteMotive extends MyModel {

//	public final static String ACCEPTED_OTHER = "motive.ac.other";
//	public final static String DELETED = "motive.deleted";
	
	@Required
	@Column(name = "name")
	public String name;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public DeleteMotive(){
	}

	public DeleteMotive(String name){
		this.name = name;
	}

	public static DeleteMotive findByName(String name) {
		return DeleteMotive.find("byName", name).first();
	}

}
