package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;
import helpers.ZipCodeHelper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import models.Activation;
import models.ActivationMotive;
import models.Consumer;
import models.ConsumerRating;
import models.Country;
import models.CountryGroup;
import models.Provider;
import models.Restriction;
import models.SourceRestriction;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.i18n.Lang;
import play.i18n.Messages;
import play.modules.excel.RenderExcel;
import play.mvc.With;
import utils.StringUtils;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Consumers extends MyController {
	
	private static final int MIN_PASSWORD_SIZE = 6;
	private static final String CONSUMERS_LIST_NAME = "UBEOS_CONSUMERS.csv";
	
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void index() {
		Logger.debug("[Consumers.index]");
		List<Consumer> consumers = Consumer.findAll();

			render(consumers);
			
		
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void index_csv() {
		Logger.debug("[Consumers.index.csv]");
		List<Consumer> consumers = Consumer.findAll();
		request.format = "csv";
		notFoundIfNull(consumers);
		renderArgs.put(RenderExcel.RA_FILENAME, CONSUMERS_LIST_NAME);
		render("Consumers/index.csv.html", consumers);

				
	}
	
	@Unrestricted
	public static void create(Consumer consumer, User user) {
		LoggerHelper.info_Logger("Creating consumer");
		
		isUserLoggedIn();
		flash.remove(Security.RENDER_ERROR);
		boolean activeHeaderRegister = true;
		
		if(session.contains("promocode")){
			String token = session.get("promocode");
			createPromo(token, consumer, user);
		}
		else {
			render(consumer, user, activeHeaderRegister);
		}
	}
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER})
	public static void show(String id) {
		LoggerHelper.info_Logger("Show consumer "+ id + " profile" );
		
		Consumer consumer = Consumer.findByAlias(id);
		if(consumer == null){
			LoggerHelper.info_Logger("Trying to view unexistent consumer profile");
			notFound();
		}
		
		List<ConsumerRating> ratings = ConsumerRating.findByConsumer(consumer);
		User user = consumer.user;
		
		render(user, consumer, ratings);
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void edit(String id) {
		LoggerHelper.info_Logger("Settings for consumer " + id);

		Consumer consumer = Consumer.findByAlias(id);
		if(consumer == null){
			LoggerHelper.info_Logger("Settings for unexistent consumer");
			notFound();
		}
		
		User user = consumer.user;
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id){
			LoggerHelper.info_Logger("Trying to edit settings for other consumer " + id);
			notFound();
		}
		
		List<Country> countries = Country.findAll();

		if(user.birthDate != null) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(user.birthDate);
		    user.birth_year = cal.get(Calendar.YEAR);
		    user.birth_month = cal.get(Calendar.MONTH)+1;
		    user.birth_day = cal.get(Calendar.DAY_OF_MONTH);
		}
	    
		render(id, consumer, user, countries);
	}

	@Unrestricted
	public static void save(@Valid Consumer consumer, @Valid User user) {
		LoggerHelper.info_Logger("Saving consumer");

		isUserLoggedIn();
		
		if(User.findByEmail(user.email)!=null){
			flash.error(Messages.get("scaffold.unique.email"));
			validation.addError("user.email", "&{scaffold.unique.email}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			
			flash.put(Security.RENDER_ERROR, true);
			
			render("@create", consumer, user);
		}
		
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
			render("@create", consumer, user);
		}
	
		if(user.password.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("user.password", "&{validation.user.pass.size}");
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			renderTemplate("Consumers/create.html", consumer, user);
		}
		
		List<String> language = request.get().acceptLanguage();
		List<String> langs = Arrays.asList(Play.configuration.getProperty("application.langs").split(","));
		String lang = "en";
		for (String l : language) {
			if(langs.contains(l)){
				lang = l;
				break;
			}
		}
		user.lang = lang;
		user.userRole = UserRole.findByName(UserRole.CONSUMER);
		user.hashPassword = User.hashPassword(user.password);
				
		user.save();
		user.refresh();

		consumer.user = user;
		consumer.save();
		
		consumer.refresh();
		String alias = helpers.ConsumerHelper.generatePromoCode(consumer);
		consumer.alias = alias; 
		consumer.save();
		
		
		String token = User.generateToken();
		ActivationMotive motive = ActivationMotive.findByMotive(ActivationMotive.ACTIVATION);
		Activation activation = new Activation(true, token, motive);
		activation.user = User.findByEmail(user.email).id;
		activation.save();
		UbeosMailer.activationConfirmation(user, activation);
		
		flash.put("success", "scaffold.register.success");
		Application.registerConfirmPending();
		
	}
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void update(Long id, @Valid Consumer consumer, @Valid User user) {
		LoggerHelper.info_Logger( "Updating consumer " + id + " settings");

		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.MONTH, user.birth_month-1);
		calendar.set(Calendar.YEAR, user.birth_year);
		calendar.set(Calendar.DAY_OF_MONTH, user.birth_day);
		user.birthDate = calendar.getTime();
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id){
			LoggerHelper.info_Logger( "Trying to update other consumer("+ id + ") settings");
			notFound();
		}
		
		if(user.zipcode!=null){
			ValidationResult a = validation.isTrue(ZipCodeHelper.isValidZip(user.zipcode));
			if(a.error != null){
				validation.addError("user.zipcode", "scaffold.wrong.format");
			}
		}

		
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			
			List countries = Country.findAll();

			LoggerHelper.info_Logger(validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			render("@edit", id, consumer, user, countries);
		}

		user.merge();
		user.save();
		consumer.merge();
		consumer.save();
		
		if(user.lang != null){
			Lang.change(user.lang);
		}
		
		flash.success(Messages.get("scaffold.updated", "Consumer"));
		edit(consumer.alias);
	}

	@RestrictedResource(name = {UserRole.ADMIN})
	public static void generatePromo(Long id) {
		LoggerHelper.info_Logger( "Generating promo code for consumer " + id );
		Consumer consumer = Consumer.findById(id);
		
		String promocode = helpers.ConsumerHelper.generatePromoCode(consumer);
		LoggerHelper.info_Logger( "Generated promo code " + promocode +" for consumer " + id );
		
		consumer.promoCode = promocode;
		consumer.save();
		flash.success(Messages.get("scaffold.updated", "Consumer"));
		index();
		
	}

	@Unrestricted
	public static void createPromo(String token, Consumer consumer, User user) {
		LoggerHelper.info_Logger("Creating consumer with promocode");
		
		Consumer referer = Consumer.findByPromoCode(token);
		if(referer == null){
			LoggerHelper.info_Logger("Tried to create consumer with promo with invalid token(code)");
			notFound();
		}
		
		isUserLoggedIn();
		flash.remove(Security.RENDER_ERROR);
		boolean activeHeaderRegister = true;
		
		session.put("promocode", token);
		
		render(token, consumer, user, activeHeaderRegister);
	}
	
	@Unrestricted
	public static void savePromo(String token, Consumer consumer, User user) {
		LoggerHelper.info_Logger("Saving consumer with promo");
		boolean activeHeaderRegister = true;
		
		isUserLoggedIn();
		
		Consumer referer = Consumer.findByPromoCode(token);
		if(referer == null){
			LoggerHelper.info_Logger("Tried to saving consumer with promo with invalid token(code)");
			notFound();
		}
		
		
		if(User.findByEmail(user.email)!=null){
			flash.error(Messages.get("scaffold.unique.email"));
			validation.addError("user.email", "&{scaffold.unique.email}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			
			flash.put(Security.RENDER_ERROR, true);
			
			render("@create",token, consumer, user, activeHeaderRegister);
		}
		
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
			render("@create",token, consumer, user, activeHeaderRegister);
		}
	
		if(user.password.length() < MIN_PASSWORD_SIZE){
			flash.error(Messages.get("validation.user.pass.size"));
			validation.addError("user.password", "&{validation.user.pass.size}");
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			renderTemplate("Consumers/create.html", token, consumer, user, activeHeaderRegister);
		}
		
		List<String> language = request.get().acceptLanguage();
		List<String> langs = Arrays.asList(Play.configuration.getProperty("application.langs").split(","));
		String lang = "en";
		for (String l : language) {
			if(langs.contains(l)){
				lang = l;
				break;
			}
		}
		user.lang = lang;
		user.userRole = UserRole.findByName(UserRole.CONSUMER);
		user.hashPassword = User.hashPassword(user.password);
				
		user.save();
		user.refresh();
		
		consumer.user = user;
		consumer.ref_from = referer.id;
		consumer.save();
				
		String token_2 = User.generateToken();
		ActivationMotive motive = ActivationMotive.findByMotive(ActivationMotive.ACTIVATION);
		Activation activation = new Activation(true, token_2, motive);
		activation.user = User.findByEmail(user.email).id;
		activation.save();
		UbeosMailer.activationConfirmation(user, activation);
		
		session.remove("promocode");
		flash.put("success", "scaffold.register.success");	
		Application.registerConfirmPending();
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void checkPromo() {
		LoggerHelper.info_Logger("Checking promo code for user");
		
		User user = UserTypeHelper.getLoggedUser(session);
		
		if(user == null){
			LoggerHelper.info_Logger("Checking promo code for invalid user");
			notFound();
		}
		
		Consumer consumer = Consumer.findByUser(user);
		if(consumer == null){
			LoggerHelper.info_Logger("Checking promo code for invalid consumer");
			notFound();
		}
		
		List<Consumer> consumers = Consumer.findByReferer(consumer.id);
		
		render(consumer, consumers, user);
	}

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void sendPromo(@Valid String email) {
		LoggerHelper.info_Logger("Sending email with promo code to: " + email);
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user==null){
			LoggerHelper.info_Logger("Tried to send promo without user");
			notFound();
		}

		Consumer consumer = Consumer.findByUser(user);
		if(consumer==null){
			LoggerHelper.info_Logger("Tried to send promocode without consumer");
			notFound();
		}
		
		if(StringUtils.isNullOrEmpty(consumer.promoCode)){
			LoggerHelper.info_Logger("Tried to send promocode without consumer have a generated promocode");
			notFound();
		}
		
		validation.required(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());

			render("@checkPromo", email);
		}
		
		validation.email(email);
		if(validation.hasErrors()){
			flash.error(Messages.get("validation.email.format"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());

			render("@checkPromo", email);
		}
				
		User user_2 = User.findByEmail(email);
		if(user_2 != null){
			validation.addError("email", "&{scaffold.promo.email.exist}");
			
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			flash.put(Security.RENDER_ERROR, true);
			flash.error(Messages.get("scaffold.promo.email.exist"));
			
			render("@checkPromo", email);
		}
		
			
		UbeosMailer.sendPromoCode(email, consumer);
		
		
		flash.success(Messages.get("email.user.sent"));
		flash.put("success", "scaffold.user.email.success");	
		checkPromo();
	
	}

}
