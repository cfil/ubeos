package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;
import helpers.ZipCodeHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import models.Activation;
import models.ActivationMotive;
import models.Company;
import models.Consumer;
import models.Country;
import models.CountryGroup;
import models.Location;
import models.MessagesRec;
import models.Provider;
import models.ProviderRating;
import models.Restriction;
import models.SourceRestriction;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import utils.StringUtils;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Providers extends MyController {
	
	private static final int MIN_PASSWORD_SIZE = 6;
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void index() {
		Logger.debug("[Providers.index]");
		List<Provider> providers = Provider.findAll();
		render(providers);
	}

	@Unrestricted
	public static void create(Provider provider, User user) {
		LoggerHelper.info_Logger("Creating a provider");
		
		isUserLoggedIn();
		
		flash.remove(Security.RENDER_ERROR);
		
		boolean activeHeaderRegister = true;
		render(provider, user, activeHeaderRegister);
	}
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void add() {
		LoggerHelper.info_Logger("Adding a provider to company");
			
		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		
		
		if(provider==null){
			LoggerHelper.info_Logger("Tried to add provider with wrong user type");
			notFound();
		}
		
		Company company = Company.findByOwner(user.id);
		if(company==null){
			LoggerHelper.info_Logger("Not a company owner");
			notFound();
		}
		
		List<Provider> providers = Provider.findByCompany(provider.company);
		
		render(company, providers);
	}
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void saveAdd(@Valid String email) {
		LoggerHelper.info_Logger("Saving a provider to company");
		
		User user_provider = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user_provider);
		if(provider==null){
			LoggerHelper.info_Logger("Tried to add provider with wrong user type");
			notFound();
		}
		
		Company company = Company.findByOwner(user_provider.id);
		if(company==null){
			LoggerHelper.info_Logger("Tried to add provider with wrong user type");
			notFound();
		}
		
		validation.required(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			List<Provider> providers = Provider.findByCompany(provider.company);
			render("@add", email, company, providers);
		}
		
		validation.email(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.email.format"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			List<Provider> providers = Provider.findByCompany(provider.company);
			render("@add", email, company, providers);
		}
				
		User user = User.findByEmail(email);
		if(user != null){
			validation.addError("email", "&{scaffold.unique.email}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			flash.error(Messages.get("scaffold.unique.email"));
			List<Provider> providers = Provider.findByCompany(provider.company);
			render("@add", email, company, providers);
		}
		
		if(user == null){
			User new_user = new User("firstName", "firstName", email);
			new_user.userRole = UserRole.findByName(UserRole.PROVIDER);
			new_user.save();
			Provider new_provider = new Provider(new_user);
			new_provider.company = company;
			new_provider.description = provider.description;
			new_provider.save();
			new_provider.refresh();
			// Create destination restrictions
			if(provider.restrictions != null){
				for (Restriction restriction : provider.restrictions) {
					Restriction newRestriction = new Restriction(restriction.country, new_provider);
					newRestriction.save();
				}
			}
			
			new_provider.save();
			new_provider.refresh();
			// Create source restrictions
			CountryGroup c_group = CountryGroup.findByName(CountryGroup.ALL);
			SourceRestriction sourceRestriction = new SourceRestriction(null, new_provider, c_group);
			sourceRestriction.save();
			new_provider.sourceRestriction = sourceRestriction;
			new_provider.save();
			
			String token = User.generateToken();
			ActivationMotive motive = ActivationMotive.findByMotive(ActivationMotive.NEW_PROVIDER);
			Activation activation = new Activation(true, token, motive);
			activation.user = User.findByEmail(new_user.email).id;
			activation.save();
			UbeosMailer.providerAdded(new_user, activation);
		} 
		
		flash.success(Messages.get("email.user.activation"));
		flash.put("success", "scaffold.user.activation.success");	
		add();

	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER, UserRole.CONSUMER})
	public static void show(String id) {
		Logger.debug("[Providers.show]");
		LoggerHelper.info_Logger( "Show profile of provider with user id: " + id);
		
		Company company = Company.findByName(id);
		if(company == null){
			LoggerHelper.info_Logger( "Try to show unexistent provider(company)");
			notFound();
		}
		
//		User user = User.findById(id);
//		if(user == null){
//			LoggerHelper.info_Logger( "Try to show unexistent provider(user)");
//			notFound();
//		}
		
		User user = User.findById(company.owner);
	
		Provider provider = Provider.findByUser(user);
		if(user == null){
			LoggerHelper.info_Logger( "Try to show unexistent provider");
			notFound();
		}
		
//		List<ProviderRating> ratings = ProviderRating.findByProvider(provider);
		List<ProviderRating> ratings = ProviderRating.findByCompany(provider.company);
		
		render(provider, ratings);
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void edit(Long id) {
		LoggerHelper.info_Logger( "Editing settings");
		
		User user = User.findById(id);
		if(user == null){
			LoggerHelper.info_Logger( "Try to edit settings of unexistent provider");
			notFound();
		}
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id && !UserTypeHelper.isAdmin(session)){
			LoggerHelper.info_Logger( "Try to edit settings of other provider");
			notFound();
		}
		
		Provider provider = Provider.find("byUser", user).first();
		List<Country> countries = Country.findAll();
		
		Company company = provider.company;
		
		render(provider, user, company, countries);
	}
	
	@Unrestricted
	public static void save(@Valid Provider provider, @Valid User user, String companyName) {
		LoggerHelper.info_Logger( "Saving provider");
		
		isUserLoggedIn();
		boolean no_padding = true;
		
		if(User.findByEmail(user.email)!=null){
			flash.error(Messages.get("scaffold.unique.email"));
			validation.addError("user.email", "&{scaffold.unique.email}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			
			renderTemplate("Application/agencybase.html", provider, user, companyName, no_padding);
		}
		
		
		ValidationResult passReq = validation.required(user.password);
		if(passReq.error != null){
			validation.addError("user.password", "&{validation.required}");
		}
		ValidationResult passConfReq = validation.required(user.passwordConfirm);
		if(passConfReq.error != null){
			validation.addError("user.passwordConfirm", "&{validation.required}");
		}
		Company c = Company.findByName(companyName);
		if(c != null){
			validation.addError("provider.company.name", "&{validation.unique}");
		}
		ValidationResult compName = validation.required(companyName);
		if(compName.error != null){
			validation.addError("provider.company.name", "&{validation.required}");
		}
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation.required"));

			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			renderTemplate("Application/agencybase.html", provider, user, companyName, no_padding);
			
		}
	
		if(user.password.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("user.password", "&{validation.user.pass.size}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			renderTemplate("Application/agencybase.html", provider, user, companyName, no_padding);
			
		}
		
		
		user.hashPassword = user.hashPassword(user.password);
		user.save();
		user.refresh();

		Company company = new Company(companyName);
		company.owner = user.id;
		company.save();
		company.refresh();
		
		provider.user = user;
		provider.company = company;
		provider.save();
		provider.refresh();
		
		CountryGroup countryGroup = CountryGroup.findByName(CountryGroup.ALL);
		SourceRestriction sourceRestriction = new SourceRestriction(null, provider, countryGroup);
		sourceRestriction.save();
		sourceRestriction.refresh();
		provider.sourceRestriction = sourceRestriction;
		provider.save();
		
		
		String token = User.generateToken();
		ActivationMotive motive = ActivationMotive.findByMotive(ActivationMotive.ACTIVATION);
		Activation activation = new Activation(true, token, motive);
		activation.user = User.findByEmail(user.email).id;
		activation.save();
		UbeosMailer.activationConfirmation(user, activation);
		
			
		flash.success(Messages.get("scaffold.created", "Provider"));
		
		Security.login();
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void update(Long id, @Valid Provider provider, @Valid User user, Company company) {
		Logger.debug("[Providers.update]");
		LoggerHelper.info_Logger( "Updating provider settings (user_id=" + id +")");
		
		if(!StringUtils.isNullOrEmpty(provider.description)){
			provider.description = provider.description.replaceAll("\n", "<br>");
		}
		
		if(user.zipcode!=null){
			ValidationResult a = validation.isTrue(ZipCodeHelper.isValidZip(user.zipcode));
			if(a.error != null){
				validation.addError("user.zipcode", "scaffold.wrong.format");
			}
		}
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			provider.refresh();
			List<Country> countries = Country.findAll();
			flash.put(Security.RENDER_ERROR, true);
			render("@edit", id, provider, user, countries);
		}

		
		user.merge();
		user.save();
		
		provider.merge();
		provider.save();
		
		company.merge();
		company.save();
		
		flash.success(Messages.get("scaffold.updated", "Provider"));
		Application.index();
		
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void restrictions(Long id) {
		LoggerHelper.info_Logger( "Updating provider restrictions (user_id=" + id +")");
		
		User user = User.findById(id);
		Provider provider = Provider.findByUser(user);
		
		List<Country> countryList = Country.findAll();
		
		render(id, provider, countryList);
	
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void saveRestrictions(Long id, List<Long> countryList) {
		LoggerHelper.info_Logger( "Saving provider restrictions (user_id=" + id +")");
				
		User user = User.findById(id);
		Provider provider = Provider.findByUser(user);
		
		Set<Restriction> restrictions = provider.restrictions;
		for (Restriction restriction : restrictions) {
			restriction.delete();
		}
		
		if( countryList != null ) {
			for (Long c_id : countryList) {
				Country country = Country.findById(c_id);
				Restriction restriction = new Restriction(country, provider);
				restriction.save();
			}
		}
		
		restrictions(id);
		
	}
	
	@Unrestricted
	public static void createNew(String token) {
		LoggerHelper.info_Logger("Creating a provider (added)");
		flash.remove(Security.RENDER_ERROR);
		
		isUserLoggedIn();
		
		Activation activation = Activation.find("byToken", token).first();
		
		if(activation == null){
			LoggerHelper.info_Logger( "Tried to activate unexistent or disabled token (provider added) : " + token);
			notFound();
		}
		
		User user = User.findById(activation.user);
		
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			LoggerHelper.info_Logger("tried to add a consumer to company");
			notFound();
		}
		
		Provider provider = Provider.findByUser(user);
		if(provider == null){
			LoggerHelper.info_Logger("Erro a acicionar novo provider a companhia");
		}
		
		user.firstName = "";
		user.lastName = "";
		
		boolean activeHeaderRegister = true;
		render(provider, user, token, activeHeaderRegister);
	}
	
	@Unrestricted
	public static void saveNew(String token, @Valid Provider provider, @Valid User user) {
		LoggerHelper.info_Logger( "Saving added provider");

		isUserLoggedIn();
		
		ValidationResult passReq = validation.required(user.password);
		if(passReq.error != null){
			validation.addError("user.password", "&{validation.required}");
		}
		ValidationResult passConfReq = validation.required(user.passwordConfirm);
		if(passConfReq.error != null){
			validation.addError("user.passwordConfirm", "&{validation.required}");
		}
			
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation.required"));

			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			boolean activeHeaderRegister = true;
			render("@createNew", provider, user, token, activeHeaderRegister);
		}
	
		if(user.password.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("user.password", "&{validation.user.pass.size}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			boolean activeHeaderRegister = true;
			render("@createNew", provider, user, token, activeHeaderRegister);
		}
		
		
		user.hashPassword = user.hashPassword(user.password);
		user.enable = true;
		user.save();

		Activation activation = Activation.findByToken(token);
		activation.delete();
		
		flash.success(Messages.get("scaffold.created", "Provider"));
		
		UbeosMailer.providerPendingAdded(user);
		
		Security.login();
	}
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void sourceRestrictions(Long id) {
		LoggerHelper.info_Logger( "Updating provider source restrictions (user_id=" + id +")");
		
		User user = User.findById(id);
		Provider provider = Provider.findByUser(user);
		
		List<Country> countryList = Country.findAll();
		List<Location> locations = Location.findAll();
		List<CountryGroup> countryGroups = CountryGroup.findAll();
		
		render(id, provider, countryList, locations, countryGroups);
	
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void saveSourceRestrictions(Long id, List<Long> locationsList) {
		LoggerHelper.info_Logger( "Saving provider source restrictions (user_id=" + id +")");
				
		User user = User.findById(id);
		Provider provider = Provider.findByUser(user);
		SourceRestriction sources = provider.sourceRestriction;
		
		
		Set<Location> newLocations = new HashSet<Location>();

		
		Query query = JPA.em().createNativeQuery("delete from source_restriction_location where source_restriction_id ="+ provider.sourceRestriction.id);
		query.executeUpdate();


		if( locationsList != null ) {

			for (Long location_id : locationsList) {
				Location location = Location.findById(location_id);
				
				newLocations.add(location);
				
			}
			sources.locations = newLocations;
			sources.save();
			provider.sourceRestriction = sources;
			provider.save();
		}
		
		sourceRestrictions(id);
		
	}
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void saveSourceGroupRestrictions(Long id, Long country_group_id) {
		LoggerHelper.info_Logger( "Saving provider source group restrictions (user_id=" + id +")");
		
		User user = User.findById(id);
		Provider provider = Provider.findByUser(user);
		SourceRestriction sourceRest = SourceRestriction.findByProvider(provider);
		
		CountryGroup countryGroup = CountryGroup.findById(country_group_id);
		
		sourceRest.countryGroup = countryGroup;
		sourceRest.save();
		
		provider.save();
		
		sourceRestrictions(id);
	}
	
	// *****************************************
	

	@Util
	public static boolean hasCountryRestriction(Provider provider, Country country){
		Set<Restriction> restrictions = provider.restrictions;
		
		for (Restriction restriction : restrictions) {
			if ( restriction.country.equals(country) ){
				return true;
			}
		}
		return false; 
		
	}
	
	@Util
	public static boolean hasLocationRestriction(Provider provider, Location location){
		SourceRestriction source_restriction = provider.sourceRestriction;
		
		// Verifica se não tem restrições de origem
		if(source_restriction == null){
			return false;
		}
		
		// Se tiver source_restrictions
		if(source_restriction.locations != null){
			// verifica se alguma das restrições é o local em causa
			if(source_restriction.locations.contains(location)){
				return true;
			} else{
				return false;
			}
		} else {
			return false;
		}
		
	}

	
}
