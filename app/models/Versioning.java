package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Unique;

@Entity
@Table(name = "versioning")
public class Versioning extends MyModel {
    	
	@Column(name = "ubeos_table")
	@Unique
	public String ubeos_table;
	
	@Column(name = "current_version")
	public Long current_version;
		
	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public Versioning() {
	}
	
	public Versioning(String ubeos_table, Long current_version) {
		super();
		this.ubeos_table = ubeos_table;
		this.current_version = current_version;
	}
	
	public static Versioning findByTableName(String table_name){
		return Versioning.find("byUbeos_table", table_name).first();
	}
	
}
