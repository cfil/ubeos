package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "accommodation", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Accommodation extends MyModel {

	@Required
	@Column(name = "name")
	public String name;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Accommodation(){
	}

	public Accommodation(String name){
		this.name = name;
	}

	public static Accommodation findByName(String name) {
		return Accommodation.find("byName", name).first();
	}

}
