package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;

@Entity
@Table(name = "consumer", uniqueConstraints={@UniqueConstraint(columnNames={"user_id"})})
public class Consumer extends MyModel {

	public static final String FEMALE = "female";
	public static final String MALE = "male";
	
	@OneToMany(mappedBy="consumer", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<TravelRequest> requests = new HashSet<TravelRequest>(0);
	
	@OneToOne
	public User user;
	
	@OneToMany(mappedBy="consumer", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<ConsumerRating> consumerRating = new HashSet<ConsumerRating>(0);
	
	@OneToMany(mappedBy="consumer", cascade = {CascadeType.REMOVE}, fetch=FetchType.LAZY)
	public Set<ProviderRating> providerRating = new HashSet<ProviderRating>(0);
	
	@Column(name="gender")
	public String gender;
	
	@OneToMany(mappedBy = "consumer")
	public Set<MessagesRec> messages = new HashSet<MessagesRec>(0);
	
	@Column(name="promo_code")
	public String promoCode = "";
	
	@Column(name="ref_from")
	public Long ref_from = null;
	
	@Column(name="alias")
	public String alias = "";

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Consumer(){
	}

	public Consumer(User user){
		this.user = user;
	}

	@Override
	public String toString() {
		return user.firstName + " " + user.lastName;
	}

	public static Consumer findByLogin(String login) {
		User user = User.findByLogin(login);
		return Consumer.find("byUser", user).first();
	}

	public static Consumer findByEmail(String email) {
		User user =  User.findByEmail(email);
		return Consumer.find("byUser", user).first();
	}
	
	public static Consumer findByUser(User user) {
		return Consumer.find("byUser", user).first();
	}

	public static Consumer findByPromoCode(String promoCode) {
		return Consumer.find("byPromoCode", promoCode).first();
	}

	public static List<Consumer> findByReferer(Long id) {
		return Consumer.find("byRef_from", id).fetch();
	}
	
	public static Consumer findByAlias(String alias) {
		return Consumer.find("byAlias", alias).first();
	}
}
