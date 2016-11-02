package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "activation_motive", uniqueConstraints=@UniqueConstraint(columnNames={"motive"}))
public class ActivationMotive extends MyModel {
    	
	
	public static final String ACTIVATION = "A";
	public static final String MESSAGE = "M";
	public static final String NEW_PROVIDER = "N";
	public static final String RECOVERY = "R";
	
	@OneToMany(mappedBy="motive", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Activation> activations;
		
	@Column(name = "motive", nullable = false)
	public String motive;
	
	//	~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public ActivationMotive() {
	}
	
	public ActivationMotive(String motive) {
		this.motive = motive;
	}
	
	public static ActivationMotive findByMotive(String motive){
		return ActivationMotive.find("byMotive", motive).first();
	}
	
}
