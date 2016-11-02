package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "meal", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Meal extends MyModel {

	@Required
	@Column(name = "name")
	public String name;
	
	@Required
	@Column(name = "enable")
	public boolean enable = true;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Meal(){
	}

	public Meal(String name){
		this.name = name;
	}

	public static Meal findByName(String name) {
		return Meal.find("byName", name).first();
	}

}
