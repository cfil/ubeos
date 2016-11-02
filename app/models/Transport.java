package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "transport", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Transport extends MyModel {

	@Required
	@Column(name = "name")
	public String name;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Transport(){
	}

	public Transport(String name){
		this.name = name;
	}

	public static Transport findByName(String name) {
		return Transport.find("byName", name).first();
	}

}
