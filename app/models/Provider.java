package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
@Table(name = "provider" , uniqueConstraints={@UniqueConstraint(columnNames={"user_id"})})
public class Provider extends MyModel {

	@ManyToOne
	public Company company;
	
	@OneToMany(mappedBy="provider", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Proposal> proposals = new HashSet<Proposal>(0);

	@OneToOne
	public User user;
	
	@OneToMany(mappedBy="provider", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<ConsumerRating> consumerRating = new HashSet<ConsumerRating>(0);
	
	@OneToMany(mappedBy="provider", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<ProviderRating> providerRating = new HashSet<ProviderRating>(0);
	
	@OneToMany(mappedBy="provider", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<Restriction> restrictions = new HashSet<Restriction>(0);
	
	@OneToOne(cascade = {CascadeType.REMOVE})
	public SourceRestriction sourceRestriction;
	
	@OneToMany(mappedBy = "provider")
	public Set<MessagesRec> messages = new HashSet<MessagesRec>(0);
	
	@Column(name = "description")
//	@MaxSize(value = 1024)
	public String description;
	
	@OneToMany(mappedBy = "provider")
	public Set<ReplyStat> replyStats = new HashSet<ReplyStat>(0);
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Provider(){
	}

	public Provider(User user){
		this.user = user;
	}

	@Override
	public String toString() {
		return company.name;
	}

	public String defaultDescription() {
		return user.firstName + " " + user.lastName;
	}

	public static Provider findByLogin(String login) {
		User user =  User.findByLogin(login);
		return Provider.find("byUser", user).first();
		
	}

	public static Provider findByEmail(String email) {
		User user =  User.findByEmail(email);
		return Provider.find("byUser", user).first();
	}
	
	public static Provider findByUser(User user) {
		return Provider.find("byUser",user).first();
	}
	
	public static List<Provider> findByCompany(Company company) {
		return Provider.find("byCompany",company).fetch();
	}
		
}
