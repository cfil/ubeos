package controllers;

import helpers.LoggerHelper;
import helpers.UserFbBuilder;
import helpers.UserTypeHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import models.Avatar;
import models.Company;
import models.Consumer;
import models.Country;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.ReplyStat;
import models.RequestState;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;

import org.apache.commons.io.IOUtils;

import play.Logger;
import play.Play;
import play.data.validation.Validation.ValidationResult;
import play.db.jpa.Blob;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Util;
import play.mvc.With;
import utils.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Application extends MyController {

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER, UserRole.CONSUMER })
    public static void index() {
    	LoggerHelper.info_Logger("Logged Index");
    	if(UserTypeHelper.isConsumer(session)){
    		LoggerHelper.info_Logger("Index for Consumer");
    		TravelRequests.index();
    	} else if(UserTypeHelper.isProvider(session)){
    		LoggerHelper.info_Logger("Index for Provider");
    		Proposals.index();
    	} else if(UserTypeHelper.isAdmin(session)){
    		LoggerHelper.info_Logger("Index for Admin");
    		   		
    		List<User> users = User.findAll();
    		
    	    render(users);
    		
    	} else {
    		LoggerHelper.info_Logger("Index for none user type");
    		notFound();
    	}
    }

    @Unrestricted
    public static void base() {
    	LoggerHelper.info_Logger("Landing page");
    	if (Security.isConnected()) {
			index();
		}
    	
//    	List<Country> countries = Country.findAll();
    	validation.clear();
    	flash.discard();
    	flash.clear();
    	boolean no_padding = true;
    	
    	HashMap testimonials = helpers.TestimonialsHelper.get_3_Testimonials();
    	
        render(no_padding, testimonials);
    }
    
    @Unrestricted
    public static void agencybase() {
    	LoggerHelper.info_Logger("Landing page provider");
    	if (Security.isConnected()) {
			index();
		}
    	
    	validation.clear();
    	flash.discard();
    	flash.clear();
    	boolean no_padding = true;
    	
    	render(no_padding);
    	
    }
    
    @Unrestricted
	public static void pending() {
    	LoggerHelper.info_Logger("Pending");
    	render();
	}

    @Unrestricted
	public static void aboutUs() {
    	LoggerHelper.info_Logger("About Us");
    	render();
	}
    
    @Unrestricted
	public static void contactUs() {
    	LoggerHelper.info_Logger("Contact Us");
    	flash.remove(Security.RENDER_ERROR);
    	render();
	}
    
    @Unrestricted
    public static void notfoundpage() {
    	LoggerHelper.info_Logger("checking 404 page");
    	renderTemplate("errors/404.html");
    }

    @Unrestricted
    public static void internalerrorpage() {
    	LoggerHelper.info_Logger("checking 500 page");
    	renderTemplate("errors/500.html");
    }
    
    @Unrestricted
	public static void terms() {
    	LoggerHelper.info_Logger("checking terms and conditions");
		String lang = Lang.get();

		if(lang!=null){
			if(lang.equals("pt")){
				LoggerHelper.info_Logger("lang is PT");
				termsPT();
			}else if(lang.equals("en")){
				LoggerHelper.info_Logger("lang is EN");
				termsEN();
			} 
		} else {
			LoggerHelper.info_Logger("lang is DEFAULT");
			termsEN();
		}
	}
    
    @Unrestricted
    public static void termsPT() {
    	LoggerHelper.info_Logger("checking terms and conditions - PT");
    	render();
    }
    
    @Unrestricted
    public static void termsEN() {
    	LoggerHelper.info_Logger("checking terms and conditions - EN");
    	render();
    }
    
    @Unrestricted
	public static void privacy() {
    	LoggerHelper.info_Logger("checking privacy");
		String lang = Lang.get();

		if(lang!=null){
			if(lang.equals("pt")){
				LoggerHelper.info_Logger("lang is PT");
				privacyPT();
			}else if(lang.equals("en")){
				LoggerHelper.info_Logger("lang is EN");
				privacyEN();
			} 
		} else {
			LoggerHelper.info_Logger("lang is DEFAULT");
			privacyEN();
		}
	}
    
    @Unrestricted
	public static void privacyPT() {
    	LoggerHelper.info_Logger("checking privacy - PT");
    	render();
	}
	
    @Unrestricted
	public static void privacyEN() {
    	LoggerHelper.info_Logger("checking privacy - EN");
    	render();
	}

    @Unrestricted
	public static void faqs() {
    	LoggerHelper.info_Logger("FAQS");
    	render();
	}
    
    @Unrestricted
	public static void media() {
    	LoggerHelper.info_Logger("MEDIA");
    	render();
	}
    
    @RestrictedResource(name = { UserRole.ADMIN })
	public static void stats() {
    	LoggerHelper.info_Logger("Index for Admin");
		
		HashMap stats = new HashMap();
		stats.put("active Requests", TravelRequest.findByState(RequestState.findByName(RequestState.ACTIVE)).size());
		stats.put("disabled Requests", TravelRequest.findByState(RequestState.findByName(RequestState.DISABLED)).size());
		stats.put("canceled Requests", TravelRequest.findByState(RequestState.findByName(RequestState.CANCELED)).size());
		stats.put("accepted Requests", TravelRequest.findByState(RequestState.findByName(RequestState.ACCEPTED)).size());
		stats.put("pending Requests", TravelRequest.findByState(RequestState.findByName(RequestState.PENDING)).size());
		stats.put("closed Requests", TravelRequest.findByState(RequestState.findByName(RequestState.CLOSED)).size());
		stats.put("feedback Requests", TravelRequest.findByState(RequestState.findByName(RequestState.FEEDBACK)).size());

		stats.put("active Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.ACTIVE)).size());
		stats.put("disabled Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.DISABLED)).size());
		stats.put("canceled Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.CANCELED)).size());
		stats.put("accepted Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.ACCEPTED)).size());
//		stats.put("pending Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.PENDING)).size());
		stats.put("closed Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.CLOSED)).size());
		stats.put("feedback Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.FEEDBACK)).size());
		stats.put("rejected Proposals", Proposal.findByState(ProposalState.findByName(ProposalState.REJECTED)).size());
		
		int total_requests = TravelRequest.findAll().size();
		int total_proposals = Proposal.findAll().size();
		int accept_reqs = TravelRequest.findByState(RequestState.findByName(RequestState.ACCEPTED)).size();
		int pend_reqs = TravelRequest.findByState(RequestState.findByName(RequestState.PENDING)).size();
		int feedb_reqs = TravelRequest.findByState(RequestState.findByName(RequestState.FEEDBACK)).size();
		
		int active_props = Proposal.findByState(ProposalState.findByName(ProposalState.ACTIVE)).size();
		int accept_props = Proposal.findByState(ProposalState.findByName(ProposalState.ACCEPTED)).size();
		int feedb_props = Proposal.findByState(ProposalState.findByName(ProposalState.FEEDBACK)).size();
		
		/* Reply Stats */
		HashMap reply_stats = new HashMap();
		List<Company> companies = Company.findAll();
		for (Company company : companies) {
			List<Provider> providers = Provider.findByCompany(company);
			for (Provider provider : providers) {
				if(provider.user.enable){
					List<ReplyStat> repliesStats = ReplyStat.findByProvider(provider);
					Long nr_received = 0L;
					Long nr_replied = 0L;
					for (ReplyStat replyStat : repliesStats) {
						nr_received++;
						if(replyStat.replied){
							nr_replied++;
						}
					}
					if(nr_received==0){nr_received = 1L;}
					if(nr_replied!=0){
						reply_stats.put(company.name, (nr_replied*100)/nr_received );
					}else{
						reply_stats.put(company.name, 0 );
					}
				}
			}
		}
		
		int total_users = User.all().fetch().size();
		
	    render(total_users, reply_stats, stats, total_requests, total_proposals, accept_reqs, pend_reqs, feedb_reqs, active_props, accept_props, feedb_props);
	}

    @Unrestricted
	public static void en() {
    	LoggerHelper.info_Logger("Set EN");
    	
    	Lang.change("en");
    	redirect(flash.get(Security.ORIGINAL_URL) != null ? flash
				.get(Security.ORIGINAL_URL) : Security.ROOT);
    	
    	
	}
    
    @Unrestricted
	public static void pt() {
    	LoggerHelper.info_Logger("Set PT");
    	
    	Lang.change("pt");
    	redirect(flash.get(Security.ORIGINAL_URL) != null ? flash
				.get(Security.ORIGINAL_URL) : Security.ROOT);
    	
	}

    @Unrestricted
	public static void contactUsSend(String name, String email, String subject, String message) {
    	LoggerHelper.info_Logger("Send mail contact us");
	
    	ValidationResult name_err = validation.required(name);
		if(name_err.error != null){
			validation.addError("name", "&{validation.required.all}");
		}
		ValidationResult email_err = validation.required(email);
		if(email_err.error != null){
			validation.addError("email", "&{validation.required.all}");
		}
		ValidationResult subject_err = validation.required(subject);
		if(subject_err.error != null){
			validation.addError("subject", "&{validation.required.all}");
		}
		ValidationResult message_err = validation.required(message);
		if(message_err.error != null){
			validation.addError("message", "&{validation.required.all}");
		}
		
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			flash.error(Messages.get("validation.required.all"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@contactUs", name, email, subject, message);
		}
		validation.clear();
		validation.email(email);
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			flash.error(Messages.get("validation.email.format"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@contactUs", name, email, subject, message);
		}
		
		if(message.length() > 300){
			validation.addError("message", "&{invalid.message}");
			flash.error(Messages.get("scaffold.validation.report.size"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			render("@contactUs",name, email, subject, message);
		}
		
		// Get Date and Time of the error
		Date date = new Date();
		
		UbeosMailer.sendContactUs(name, email, subject, message, date);
		UbeosMailer.sendContactUsOrigin(name, email, subject, message, date);
		
		flash.put(Security.RENDER_ERROR, true);
		flash.success(Messages.get("scaffold.contact.us.sent"));
		contactUs();
	}
    
    @Unrestricted
    public static void underWork() {
    	LoggerHelper.info_Logger("under work");
    	renderTemplate("system_down.html");
    }

    @Unrestricted
	public static void testimonials() {
    	LoggerHelper.info_Logger("MEDIA");
    	
    	HashMap testimonials = helpers.TestimonialsHelper.getAllTestimonials();
    	
    	render(testimonials);
	}
    
    @Unrestricted
    public static void lastMinutePromo(){
    	LoggerHelper.info_Logger("Last Minute Promo");
    	
    	if(!helpers.TemplatesHelper.hasLastMinute()){
    		notFound();
    	}
    	
    	render();
    }
    
    @Unrestricted
    public static void lastMinutePromoInfo(){
    	LoggerHelper.info_Logger("Last Minute Promo Info");
    	if(!helpers.TemplatesHelper.hasLastMinute()){
    		notFound();
    	}
    	render();
    }
    
    @Unrestricted
    public static void lastMinutePromoSave(String firstname,String lastname, String email, Long phone){
    	if(!helpers.TemplatesHelper.hasLastMinute()){
    		notFound();
    	}
    	LoggerHelper.info_Logger("Save Last Minute Promo: "+firstname+" "+lastname+" - "+email+" : "+phone);
    	
    	ValidationResult fname_err = validation.required(firstname);
		if(fname_err.error != null){
			validation.addError("firstname", "&{validation.required.all}");
		}
		ValidationResult lname_err = validation.required(lastname);
		if(lname_err.error != null){
			validation.addError("lastname", "&{validation.required.all}");
		}
		ValidationResult email_err = validation.required(email);
		if(email_err.error != null){
			validation.addError("email", "&{validation.required.all}");
		}
		ValidationResult phone_err = validation.required(phone);
		if(phone_err.error != null){
			validation.addError("phone", "&{validation.required.all}");
		}
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			
			flash.error(Messages.get("validation.required.all"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			boolean ischaves = true;
			render("@lastMinutePromo", firstname, lastname, email, phone, ischaves);
		}
		validation.clear();
		validation.email(email);
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			
			flash.error(Messages.get("validation.email.format"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			boolean ischaves = true;
			render("@lastMinutePromo",firstname, lastname, email, phone, ischaves);
		}
				
		UbeosMailer.sendLastMinutePromoProvider(firstname, lastname, email, phone);
				
		flash.put(Security.RENDER_ERROR, true);
		flash.success(Messages.get("last.minute.promo.mail.sent"));
		
		Consumers.create(null, null);
		
    }
    
    @Unrestricted
    public static void lastMinutePromoSave2(String firstname,String lastname, String email, Long phone){
    	if(!helpers.TemplatesHelper.hasLastMinute()){
    		notFound();
    	}
    	LoggerHelper.info_Logger("Save Last Minute Promo: "+firstname+" "+lastname+" - "+email+" : "+phone);
    	
    	ValidationResult fname_err = validation.required(firstname);
		if(fname_err.error != null){
			validation.addError("firstname", "&{validation.required.all}");
		}
		ValidationResult lname_err = validation.required(lastname);
		if(lname_err.error != null){
			validation.addError("lastname", "&{validation.required.all}");
		}
		ValidationResult email_err = validation.required(email);
		if(email_err.error != null){
			validation.addError("email", "&{validation.required.all}");
		}
		ValidationResult phone_err = validation.required(phone);
		if(phone_err.error != null){
			validation.addError("phone", "&{validation.required.all}");
		}
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			
			flash.error(Messages.get("validation.required.all"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			boolean isacores = true;
			render("@lastMinutePromo", firstname, lastname, email, phone, isacores);
		}
		validation.clear();
		validation.email(email);
		if (validation.hasErrors()) {
			
			flash.put(Security.RENDER_ERROR, true);
			
			flash.error(Messages.get("validation.email.format"));
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			boolean isacores = true;
			render("@lastMinutePromo",firstname, lastname, email, phone, isacores);
		}
				
		UbeosMailer.sendLastMinutePromoProvider(firstname, lastname, email, phone);
				
		flash.put(Security.RENDER_ERROR, true);
		flash.success(Messages.get("last.minute.promo.mail.sent"));
		
		Consumers.create(null, null);
		
    }
    
	@RestrictedResource(name = {UserRole.ADMIN})
	public static void generateAliasForAllConsumers() {
		LoggerHelper.info_Logger( "Generating alias for all consumers");
		
		List<Consumer> consumers = Consumer.findAll();
		for (Consumer consumer : consumers) {
			if(StringUtils.isNullOrEmpty(consumer.alias)){
				String alias = helpers.ConsumerHelper.generatePromoCode(consumer);
				consumer.alias = alias;
				consumer.save();
			}
		}
			
		index();
	}
		
    @Unrestricted
    public static void providerAlert() {
    	LoggerHelper.info_Logger("Provider blocked alert");
    	render();
    }
    
    @Unrestricted
    public static void registerConfirmPending() {
    	LoggerHelper.info_Logger("Register Confirm Pending");
    	render();
    }
    
    @Unrestricted
	public static void fblogin(JsonObject body) throws IOException, SQLException {
		LoggerHelper.info_Logger("Logging: "+body);
		
		UserFbBuilder user_fb = new Gson().fromJson(body, UserFbBuilder.class);
		
		User user = User.findByEmail(user_fb.getEmail());
		
		
		if(user == null){
			user = createUserFromFB(user_fb);
			saveAvatarFromFb(user, user_fb);
		} else {
			if(user.userRole.equals(UserRole.findByName(UserRole.PROVIDER))){
					flash.error(Messages.get("provider.not.fb.active"));
					flash.keep(Security.ORIGINAL_URL);
					flash.put(Security.RENDER_ERROR, true);
					renderTemplate("Security/login.html");
			}
			Avatar avatar = Avatar.findByUser(user);
			if(avatar==null){
				saveAvatarFromFb(user, user_fb);	
			}
		}
		
		Boolean allowed = true;
		
		if(user.fb_id == null){
			allowed = false;
		}
		
		// Allow only enabled users
		if (allowed && !user.enable) {
			allowed = false;
			flash.error(Messages.get("security.not.active"));
			flash.keep(Security.ORIGINAL_URL);
			flash.put("username", user.email);
			flash.put(Security.RENDER_ERROR, true);
			renderTemplate("Security/login.html");
		}

		// Language
		// If user has a supported language it uses that one, otherwise use the
		// browser lang
		if (user.lang != null) {
			Lang.change(user.lang);
		}

		// Mark user as connected
		session.put(Security.USER_COOKIE, user.id);
		LoggerHelper.info_Logger(session.current().all().toString());

		// make the user available in templates
		renderArgs.put(Security.USER, user);
		
		LoggerHelper.info_Logger("Go to Index");
		index();
	}

	@Util
    private static User createUserFromFB(UserFbBuilder user_fb){
    	
    	List<String> language = request.get().acceptLanguage();
		List<String> langs = Arrays.asList(Play.configuration.getProperty("application.langs").split(","));
		String lang = "en";
		for (String l : language) {
			if(langs.contains(l)){
				lang = l;
				break;
			}
		}
		
		User user = new User();
		user.lang = lang;
		user.userRole = UserRole.findByName(UserRole.CONSUMER);
		user.email = user_fb.getEmail();				
		user.firstName = user_fb.getFname().split(" ", 2)[0];
		user.lastName = user_fb.getFname().split(" ", 2)[1];
		user.enable = true;
		user.fb_id = Long.parseLong(user_fb.getId());
		
		if(utils.StringUtils.isNullOrEmpty(user_fb.getBdate())){
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			try {
				date = format.parse(user_fb.getBdate());
				user.birthDate = date;
			} catch (ParseException e) {
				LoggerHelper.error_Logger("Failed to get birthdate");
			}			
		}
		
		user.save();
		user.refresh();

		Consumer consumer = new Consumer();
		consumer.user = user;
		consumer.save();
		
		consumer.refresh();
		String alias = helpers.ConsumerHelper.generatePromoCode(consumer);
		consumer.alias = alias; 
		consumer.save();
		
		UbeosMailer.welcomeFromFb(user);
		return user;

    }
    
    @Util
    private static void saveAvatarFromFb(User user, UserFbBuilder user_fb){

    	try {
    	
			String url = "https://graph.facebook.com/"+user_fb.getId()+"/picture?type=large";
			URL obj;
				obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
	
			int responseCode = con.getResponseCode();
			if(responseCode==200){
				InputStream is = con.getInputStream();
				Blob b = new Blob();
				b.set(is, "image/png");
				
				Avatar avatar = Avatar.findByUser(user);
				if(avatar != null){
					avatar.avatar = b;
					avatar.save();					
				} else {
					Avatar av2 = new Avatar();
					av2.user = user;
					av2.avatar = b;
					av2.save();
				}
			}
    	} catch (Exception e) {
    		LoggerHelper.error_Logger(e.getMessage());
    	} 
    }

}