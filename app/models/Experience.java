package models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
@Table(name = "experience", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Experience extends MyModel {

	@Required
	@Column(name = "name")
	public String name;
		
	@Required
	@Column(name = "enable")
	public boolean enable = true;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "experiences")
	public Set<TravelRequest> requests;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "experiences")
	public Set<TravelRequest> proposals;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Experience(){
	}

	public Experience(String name){
		this.name = name;
	}

	public static Experience findByName(String name) {
		return Experience.find("byName", name).first();
	}

}
