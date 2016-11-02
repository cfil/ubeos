package controllers;

import helpers.LoggerHelper;
import helpers.RequestHelper;
import helpers.UserTypeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Accommodation;
import models.Activation;
import models.ActivationMotive;
import models.Comment;
import models.Consumer;
import models.Country;
import models.CountryGroup;
import models.DeleteMotive;
import models.Experience;
import models.Location;
import models.Meal;
import models.MessagesRec;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.ReplyStat;
import models.RequestState;
import models.Restriction;
import models.SourceRestriction;
import models.Transport;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.cache.Cache;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Util;
import play.mvc.With;
import utils.StringUtils;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class TravelRequests extends MyController {
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void index() {
		LoggerHelper.info_Logger( "list requests");
		
		// Get Logged user
		User user = User.findById(Long.parseLong(session.get("ubeos_user")));
		isValidUser(user);
		
		// Get Travel Requests accordingly with user profile
		List<TravelRequest> requests = new ArrayList<TravelRequest>();
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			Consumer consumer = Consumer.find("byUser_id",session.get("ubeos_user")).first();
			RequestState state = RequestState.findByName(RequestState.ACTIVE);
			requests = TravelRequest.find("byConsumerAndEnableAndState", consumer, true, state).fetch();			
		} else if (user.userRole.name.equals(UserRole.ADMIN) ){
			requests = TravelRequest.findAll();
		}
		
		// Set active tabs and menu
		String activeTab = "index";
		String activeHeader = "dashboard";

		render(requests, activeTab, activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void cancel() {
		LoggerHelper.info_Logger( "Canceling Request");
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			// Erase it from cache
			Cache.delete(session.getId());
		}
		
		index();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createStep1(String to_country, String to_city, TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step 1");
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
		}
		
		List<Country> countries = Country.findAll();
		List<Location> locations = Location.findByCountry(Country.findByCode("PT"));
		
		Country country = Country.findByCode(to_country);

		// If it is first time on step 1 and comes from landing page, set highlights. Otherwise keep null
		if(cached==null){
			travelRequest = new TravelRequest();
			travelRequest.country_to = country;
			travelRequest.city_to = to_city;
			travelRequest.validTo = 2L;
		}
		
		// Set active header
		String activeHeader = "createReq";
		render(travelRequest, countries, locations, activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createStep2(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step 2");
		
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
			Cache.replace(session.getId(), travelRequest);
			if(travelRequest.adults == null){
				travelRequest.adults = 1L;
			}
			if(travelRequest.children == null){
				travelRequest.children = 0L;
			}
			
		} else { // To avoid errors on slow submits
			travelRequest.nights = 1;
			if(travelRequest.adults == null){
				travelRequest.adults = 1L;
			}
			if(travelRequest.children == null){
				travelRequest.children = 0L;
			}
		}
		
		List<Country> countries = Country.findAll();
		
		// Set active header
		String activeHeader = "createReq";
		render(travelRequest, countries, activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createStep3(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step 3");

		// Set active header
		String activeHeader = "createReq";

		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
			Cache.replace(session.getId(), travelRequest);
			
			if(travelRequest.adults == null){
				travelRequest.adults = 1L;
			}
			if(travelRequest.children == null){
				travelRequest.children = 0L;
			}
		} else { // To avoid errors on slow submits
			if(travelRequest.adults == null){
				travelRequest.adults = 1L;
			}
			if(travelRequest.children == null){
				travelRequest.children = 0L;
			}
			
		}
		
		List<Transport> transports = Transport.findAll();
		
		
		render(travelRequest, transports,  activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createStep4(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step 4");
		
		// Set active header
		String activeHeader = "createReq";
		int stepBack = 2;
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
			Cache.replace(session.getId(), travelRequest);
			if(travelRequest.wantTransportation){
				stepBack = 3;
			} 
		}
		
		List<Accommodation> accommodations = Accommodation.findAll();
		List<Meal> meals = Meal.findAll();
		
		
		render(travelRequest, accommodations, meals, activeHeader, stepBack);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createStep5(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step 5");
		
		// Set active header
		String activeHeader = "createReq";
		int stepBack = 2;
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
			Cache.replace(session.getId(), travelRequest);
			if(travelRequest.wantAccommodation){
				stepBack = 4;
			} else if(travelRequest.wantTransportation){
				stepBack = 3;
			} 
		} else {
			notFound();
		}
		
		List<Experience> experiences = Experience.findAll();
		
		render(travelRequest,  activeHeader, experiences, stepBack);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })	
	public static void createResume(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Creating request Step Resume");
		
		// Set active header
		String activeHeader = "createReq";
		int stepBack = 2;
		
		// Get Travel Request from cache
		Object cached = Cache.get(session.getId());
		if(cached != null){
			travelRequest = (TravelRequest) cached;
			Cache.replace(session.getId(), travelRequest);
			if(travelRequest.wantActivities){
				stepBack = 5;
			} else if(travelRequest.wantAccommodation){
				stepBack = 4;
			} else if(travelRequest.wantTransportation){
				stepBack = 3;
			} 
		} else {
			notFound();
		}
		
		List<Experience> experiences = Experience.findAll();
		
		render(travelRequest,  activeHeader, experiences, stepBack);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER, UserRole.PROVIDER })
	public static void show(Long id) {
		LoggerHelper.info_Logger( "View request: " + id);
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		if(travelRequest == null){
			notFound();
		}
		
		// Get Proposals and comments accordingly with user profile
		List<Proposal> proposals = new ArrayList<Proposal>();
		List<Comment> c_comments = new ArrayList<Comment>();
		
		if(helpers.UserTypeHelper.isConsumer(session)){
			User user = travelRequest.consumer.user;
			isValidUser(user);
			if(user != UserTypeHelper.getLoggedUser(session)){
				notFound();
			}
			
			isValidRequest(travelRequest);
			
			User logged = UserTypeHelper.getLoggedUser(session);
			if(logged.id != user.id){
				LoggerHelper.info_Logger( "Tried to view other consumer request");
				notFound();
			}
			proposals = Proposal.findByRequest(travelRequest);

		} else if(helpers.UserTypeHelper.isProvider(session)){
			isValidRequest(travelRequest);
			
			User user = UserTypeHelper.getLoggedUser(session);
			Provider provider = Provider.findByUser(user);
			
			proposals = Proposal.findByRequestAndCompany(travelRequest, provider.company);
			c_comments = Comment.getCommentByCompanyAndRequest(provider.company, travelRequest);
			if(proposals.size()==0){
				Proposals.create(travelRequest.id, null);
			}
		} else {
			proposals = Proposal.findByRequest(travelRequest);
		}
		

		render(travelRequest, proposals, c_comments);
	}

	@RestrictedResource(name = { UserRole.ADMIN })
	public static void edit(Long id) {
		LoggerHelper.info_Logger("Editing request: " + id);
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		
		List<Experience> experiences = Experience.findAll();
		List<Country> countries = Country.findAll();
		List<Location> locations = Location.findAll();
		List<Transport> transports = Transport.findAll();
		List<Accommodation> accommodations = Accommodation.findAll();
		List<Meal> meals = Meal.findAll();
				
		render(travelRequest, experiences, countries, locations, transports, accommodations, meals);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER})
	public static void delete(Long id) {
		LoggerHelper.info_Logger( "Delete Request " + id);
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		isValidRequest(travelRequest);
		
		if(travelRequest.state != RequestState.findByName(RequestState.ACTIVE)){
			LoggerHelper.info_Logger("Tried to deleted closed request");
			TravelRequests.index();
		}
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user.userRole.name.equals(UserRole.CONSUMER) ){
			Consumer consumer = travelRequest.consumer;
			if(user != consumer.user){
				LoggerHelper.info_Logger( "Trying to delete request of other user");
				notFound();
			}
		}
		
		List<DeleteMotive> motives = DeleteMotive.findAll();
		
		render(travelRequest, motives);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER})
	public static void deleteSave(Long id, Long motive_id, String message) {
		LoggerHelper.info_Logger( "Deleting request: " + id);
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		isValidRequest(travelRequest);
		
		User user = travelRequest.consumer.user;
		isValidUser(user);
		
		if(travelRequest.state != RequestState.findByName(RequestState.ACTIVE)){
			LoggerHelper.info_Logger("Tried to deleted closed request");
			
			flash.error(Messages.get("scaffold.validation.delete.closed"));
			flash.put(Security.RENDER_ERROR, true);
			
			TravelRequests.index();
		}
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(logged.id != user.id){
			LoggerHelper.info_Logger( "Tried to delete other consumer request");
			notFound();
		}
		
		if(!utils.CommUtils.checkValidMessage(message) || message.length() > 300){
			validation.addError("message", "&{invalid.message}");
			flash.error(Messages.get("scaffold.validation.contacts"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			List<DeleteMotive> motives = DeleteMotive.findAll();
			
			render("@delete",id,travelRequest, message, motives);
		}
		
		if(motive_id==null){
			validation.addError("motive_id", "&{scaffold.validation}");
			flash.error(Messages.get("scaffold.validation"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger(message + " -> " + validation.errorsMap().toString());
			
			List<DeleteMotive> motives = DeleteMotive.findAll();
			
			render("@delete",id,travelRequest, message, motives);
		}
				
		List <Proposal> proposals = Proposal.findByRequest(travelRequest);
		for (Proposal prop : proposals) {
			Proposals.deleteRejectProposalAndNotify(prop,motive_id,message);
		}
		
		travelRequest.state = RequestState.findByName(RequestState.DISABLED);
		travelRequest.enable = false;
		travelRequest.save();
		
		List<ReplyStat> replyStats = ReplyStat.findByRequestID(travelRequest.id);
		for (ReplyStat replyStat : replyStats) {
			replyStat.enable = false;
			replyStat.save();
		}
		
		flash.put("success", "scaffold.request.deleted.success");	
		index();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveStep1(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Saving Step 1");
		String activeHeader = "createReq";

		validateStep1(travelRequest);
		
		boolean contactsError = false;
		ValidationResult descriptionValid = validation.isTrue(utils.CommUtils.checkValidMessage(travelRequest.description));
		if(descriptionValid.error != null){
			validation.addError("travelRequest.description", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		
		if (validation.hasErrors()) {
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			
			List<Country> countries = Country.findAll();
			List<Location> locations = Location.findByCountry(Country.findByCode("PT"));
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@createStep1", travelRequest, countries, locations, activeHeader);
		}
		
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cached = (TravelRequest) cached;
			travelRequest_cached.title = travelRequest.title;
			travelRequest_cached.country_location = travelRequest.country_location;
			travelRequest_cached.location = travelRequest.location;
			travelRequest_cached.maxBudget = travelRequest.maxBudget; // Preference
			travelRequest_cached.validTo = travelRequest.validTo;
			travelRequest_cached.wantAccommodation = travelRequest.wantAccommodation;
			travelRequest_cached.wantTransportation = travelRequest.wantTransportation;
			travelRequest_cached.wantActivities = travelRequest.wantActivities;
			Cache.replace(session.getId(), travelRequest_cached);
		} else{
			Cache.set(session.getId(), travelRequest, "30mn");
		}
		
		createStep2(null);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveStep2(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Saving Step 2");
		String activeHeader = "createReq";

		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			travelRequest.dateFrom = new Date(formatter.parse(travelRequest.date_from).getTime());
			travelRequest.dateTo = new Date(formatter.parse(travelRequest.date_to).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		validateStep2(travelRequest);

		boolean contactsError = false;
		ValidationResult descriptionValid = validation.isTrue(utils.CommUtils.checkValidMessage(travelRequest.descriptionTwo));
		if(descriptionValid.error != null){
			validation.addError("travelRequest.descriptionTwo", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		boolean datesError = false;
		if(travelRequest.dateFrom != null && travelRequest.dateTo != null){
			if(travelRequest.dateTo.getTime() < travelRequest.dateFrom.getTime()){
				datesError = true;
			}
		}
		
		if (validation.hasErrors()) {
			
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else if(datesError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.dates.interval"));
			} else{
				flash.error(Messages.get("scaffold.validation.required"));
			}
						
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			List<Country> countries = Country.findAll();
			
			render("@createStep2", travelRequest, countries, activeHeader);
		}
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cached = (TravelRequest) cached;
			travelRequest_cached.country_to = travelRequest.country_to;
			travelRequest_cached.city_to = travelRequest.city_to;
			travelRequest_cached.dateFrom = travelRequest.dateFrom;
			travelRequest_cached.dateTo = travelRequest.dateTo;
			travelRequest_cached.date_from = travelRequest.date_from;
			travelRequest_cached.date_to = travelRequest.date_to;
			travelRequest_cached.nights = travelRequest.nights;
			travelRequest_cached.adults = travelRequest.adults;
			travelRequest_cached.children = travelRequest.children;
			travelRequest_cached.descriptionPeople = travelRequest.descriptionPeople;
			travelRequest_cached.flexible = travelRequest.flexible;
			
			Cache.replace(session.getId(), travelRequest_cached, "30mn");
			if(travelRequest_cached.wantTransportation){
				createStep3(null);
			} else if (travelRequest_cached.wantAccommodation){
				createStep4(null);
			} else if (travelRequest_cached.wantActivities){
				createStep5(null);
			}

		
		} else{
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( "Too much time submiting a request");
			index();
		}
		
		
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveStep3(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Saving Step 3");
		String activeHeader = "createReq";

		validateStep3(travelRequest);

		boolean contactsError = false;
		ValidationResult descriptionValid = validation.isTrue(utils.CommUtils.checkValidMessage(travelRequest.descriptionThree));
		if(descriptionValid.error != null){
			validation.addError("travelRequest.descriptionThree", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		
		if (validation.hasErrors()) {
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			
			List<Transport> transports = Transport.findAll();
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@createStep3", travelRequest, transports, activeHeader);
		}
		
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cached = (TravelRequest) cached;
			travelRequest_cached.transport = travelRequest.transport;
			travelRequest_cached.descriptionThree = travelRequest.descriptionThree;
			Cache.replace(session.getId(), travelRequest_cached, "30mn");
			
			
			if (travelRequest_cached.wantAccommodation){
				createStep4(null);
			} else if (travelRequest_cached.wantActivities){
				createStep5(null);
			} else {
				createResume(null);
			}
			
		} else{
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( "Too much time submiting a request");
			index();
		}
		
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveStep4(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Saving Step 4");
		
		validateStep4(travelRequest);
		
		boolean contactsError = false;
		ValidationResult roomTypeValid = validation.isTrue(utils.CommUtils.checkValidMessage(travelRequest.roomType));
		if(roomTypeValid.error != null){
			validation.addError("travelRequest.roomType", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		if (validation.hasErrors()) {
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			String activeHeader = "createReq";
						
			List<Accommodation> accommodations = Accommodation.findAll();
			List<Meal> meals = Meal.findAll();
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@createStep4", travelRequest, activeHeader, accommodations, meals);
		}
		
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cached = (TravelRequest) cached;
			travelRequest_cached.accommodation = travelRequest.accommodation;
			travelRequest_cached.meal = travelRequest.meal;
			travelRequest_cached.stars = travelRequest.stars;
			travelRequest_cached.roomType = travelRequest.roomType;
			Cache.replace(session.getId(), travelRequest_cached, "30mn");
			
			if (travelRequest_cached.wantActivities){
				createStep5(null);
			} else {
				createResume(null);
			}
			
		} else{
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( "Too much time submiting a request");
			index();
		}
		
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveStep5( TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "Saving Step 5");
				
		validateStep5(travelRequest);
		
		boolean contactsError = false;
		ValidationResult descriptionValid = validation.isTrue(utils.CommUtils.checkValidMessage(travelRequest.descriptionFour));
		if(descriptionValid.error != null){
			validation.addError("travelRequest.descriptionFour", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		if (validation.hasErrors()) {
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			String activeHeader = "createReq";
						
			List<Experience> experiences = Experience.findAll();
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@createStep5", travelRequest, activeHeader, experiences);
		}
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cached = (TravelRequest) cached;
			travelRequest_cached.experiences = travelRequest.experiences;
			travelRequest_cached.descriptionFour = travelRequest.descriptionFour;
			Cache.replace(session.getId(), travelRequest_cached, "30mn");
		} else{
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( "Too much time submiting a request");
			index();
		}
		
		createResume(null);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void saveResume(@Valid TravelRequest travelRequest2) {
		LoggerHelper.info_Logger( "Saving Resume");
		
		Object cached = Cache.get(session.getId());
		if(cached != null){
			TravelRequest travelRequest_cahed = (TravelRequest) cached;
			Cache.delete(session.getId());
		
			User user = UserTypeHelper.getLoggedUser(session);
			Consumer consumer = Consumer.findByUser(user);
			
			travelRequest_cahed.state = RequestState.findByName(RequestState.ACTIVE);
			travelRequest_cahed.consumer = consumer;
			travelRequest_cahed.image = RequestHelper.getNextRequestImage(consumer);
			
			
			if(!StringUtils.isNullOrEmpty(travelRequest_cahed.description)){
				travelRequest_cahed.description = travelRequest_cahed.description.replaceAll("\n", "<br>");
			}
			if(!StringUtils.isNullOrEmpty(travelRequest_cahed.descriptionTwo)){
				travelRequest_cahed.descriptionTwo = travelRequest_cahed.descriptionTwo.replaceAll("\n", "<br>");
			}
			if(!StringUtils.isNullOrEmpty(travelRequest_cahed.descriptionThree)){
				travelRequest_cahed.descriptionThree = travelRequest_cahed.descriptionThree.replaceAll("\n", "<br>");
			}
			if(!StringUtils.isNullOrEmpty(travelRequest_cahed.descriptionFour)){
				travelRequest_cahed.descriptionFour = travelRequest_cahed.descriptionFour.replaceAll("\n", "<br>");
			}
			if(!StringUtils.isNullOrEmpty(travelRequest_cahed.roomType)){
				travelRequest_cahed.roomType = travelRequest_cahed.roomType.replaceAll("\n", "<br>");
			}
			
			travelRequest_cahed.save();
			
			notifySuitedProviders(travelRequest_cahed);
			
			flash.success(Messages.get("success.request.created"));
			index();
		} else {
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( "Too much time submiting a request");
			index();
		}
	}

	@RestrictedResource(name = { UserRole.ADMIN })
	public static void update(Long id, @Valid TravelRequest travelRequest) {
		LoggerHelper.info_Logger("Updating request: " + travelRequest.id);
						
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			flash.put(Security.RENDER_ERROR, true);
			
			List<Experience> experiences = Experience.findAll();
			List<Country> countries = Country.findAll();
			List<Location> locations = Location.findAll();
			List<Transport> transports = Transport.findAll();
			List<Accommodation> accommodations = Accommodation.findAll();
			List<Meal> meals = Meal.findAll();
			
			render("@edit", travelRequest, experiences, countries, locations, transports, accommodations, meals);
		}
		
		travelRequest.merge();
		travelRequest.save();
		
		flash.success(Messages.get("scaffold.updated", "TravelRequest"));
		Application.index();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void history() {
		LoggerHelper.info_Logger( "Show requests history");
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
				
		List<TravelRequest> requests = new ArrayList<TravelRequest>();
		if(!user.userRole.name.equals(UserRole.CONSUMER) && !user.userRole.name.equals(UserRole.ADMIN)){
			LoggerHelper.info_Logger( "Tried to show other consumer history");
			notFound();
		}
			
		Consumer consumer = Consumer.findByUser(user);
		requests = TravelRequest.find("byConsumerAndState", consumer, RequestState.findByName(RequestState.CLOSED)).fetch();			

		String activeTab = "history";
		String activeHeader = "dashboard";

		renderTemplate("TravelRequests/index.html",requests, activeTab, activeHeader);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void accepted() {
		LoggerHelper.info_Logger( "Show accepted history");
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		List<TravelRequest> requests = new ArrayList<TravelRequest>();
		if(!user.userRole.name.equals(UserRole.CONSUMER) && !user.userRole.name.equals(UserRole.ADMIN)){
			LoggerHelper.info_Logger( "Tried to show other consumer accepted history");
			notFound();
		}
			
		Consumer consumer = Consumer.findByUser(user);
		List<TravelRequest> requestsConsumer = TravelRequest.find("consumer_id = ? order by created_at desc",consumer).fetch();
		for (TravelRequest travelRequest : requestsConsumer) {
			if(travelRequest.state.requestState.equals(RequestState.ACCEPTED) || travelRequest.state.requestState.equals(RequestState.PENDING) || travelRequest.state.requestState.equals(RequestState.FEEDBACK)){
				requests.add(travelRequest);
			}
		}
			
		String activeTab = "accepted";
		String activeHeader = "dashboard";

		renderTemplate("TravelRequests/index.html",requests, activeTab, activeHeader);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void search() {
		LoggerHelper.info_Logger( "Search Requests");

		List<TravelRequest> allRequests = TravelRequest.find("state_id = ? order by updated_at desc",RequestState.findByName(RequestState.ACTIVE)).fetch();
		String activeTab = "findAdventures";
		
		User user = helpers.UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		
		List<TravelRequest> requests = getFilteredRequests(allRequests, provider);
		requests = Proposal.removeIgnored(requests, provider);
		
		render(requests, activeTab);

		
	}

	@Unrestricted
	public static void requestDeleted() {
		LoggerHelper.info_Logger("Show Request Deleted Message");
		render();
	}
	
	/* ******************************************************************************
	 * ******************************* AUXILIARES *******************************
	 * ****************************************************************************** */	
	
	@Util
	private static void validateStep1(TravelRequest travelRequest){
		ValidationResult title = validation.required(travelRequest.title);
		if(title.error != null){
			validation.addError("travelRequest.title", "&{validation.required}");
		}
		ValidationResult coutry_location = validation.required(travelRequest.country_location);
		if(coutry_location.error != null){
			validation.addError("travelRequest.country_location", "&{validation.required}");
		}
		if(travelRequest.country_location.code.equals("PT")){
			ValidationResult location = validation.required(travelRequest.location);
			if(location.error != null){
				validation.addError("travelRequest.location", "&{validation.required}");
			}
		}
		ValidationResult maxBudget = validation.required(travelRequest.maxBudget);
		if(maxBudget.error != null){
			validation.addError("travelRequest.maxBudget", "&{validation.required}");
		}
		ValidationResult validTo = validation.required(travelRequest.validTo);
		if(validTo.error != null){
			validation.addError("travelRequest.validTo", "&{validation.required}");
		}
		if(travelRequest.wantAccommodation == false && travelRequest.wantActivities == false && travelRequest.wantTransportation == false){
			validation.addError("travelRequest.wants", "&{validation.required}");
		}
		
	}
	
	@Util
	private static void validateStep2(TravelRequest travelRequest){
		ValidationResult country_to = validation.required(travelRequest.country_to);
		if(country_to.error != null){
			validation.addError("travelRequest.country_to", "&{validation.required}");
		}
		ValidationResult city_to = validation.required(travelRequest.city_to);
		if(city_to.error != null){
			validation.addError("travelRequest.city_to", "&{validation.required}");
		}
		ValidationResult nights = validation.min(travelRequest.nights, 0);
		if(nights.error != null){
			validation.addError("travelRequest.nights", "&{validation.required}");
		}
		ValidationResult dateFrom = validation.required(travelRequest.dateFrom);
		if(dateFrom.error != null){
			validation.addError("travelRequest.dateFrom", "&{validation.required}");
		}
		ValidationResult dateTo = validation.required(travelRequest.dateTo);
		if(dateTo.error != null){
			validation.addError("travelRequest.dateTo", "&{validation.required}");
		}
		if(travelRequest.dateFrom != null && travelRequest.dateTo != null){
			if(travelRequest.dateTo.getTime() < travelRequest.dateFrom.getTime()){
				validation.addError("travelRequest.dateTo", "&{scafold.validation}");
				validation.addError("travelRequest.dateFrom", "&{scafold.validation}");
				flash.error("&{scafold.validation}");
			} else {
				long diff = travelRequest.dateTo.getTime() - travelRequest.dateFrom.getTime();
				diff = diff / (1000 * 60 * 60 * 24);
				if(travelRequest.nights > diff){
					validation.addError("travelRequest.nights", "&{invalid.value}");
				}
			}
		}
		ValidationResult people = validation.isTrue(travelRequest.descriptionPeople.length() <= TravelRequest.MAX_DESCRIPTION_SIZE );
		if(people.error != null){
			validation.addError("travelRequest.descriptionPeople", "&{validation.required}");
		}
		if(travelRequest.children > 0){
			ValidationResult descriptionPeople = validation.required(travelRequest.descriptionPeople);
			if(descriptionPeople.error != null){
				validation.addError("travelRequest.descriptionPeople", "&{validation.required}");
			}	
		}
		
	}
	
	@Util
	private static void validateStep3(TravelRequest travelRequest){
		ValidationResult transport = validation.required(travelRequest.transport);
		if(transport.error != null){
			validation.addError("travelRequest.transport", "&{validation.required}");
		}
		ValidationResult descriptionThree = validation.isTrue(travelRequest.descriptionThree.length() <= TravelRequest.MAX_DESCRIPTION_SIZE );
		if(descriptionThree.error != null){
			validation.addError("travelRequest.descriptionThree", "&{validation.required}");
		}

	}

	@Util
	private static void validateStep4(TravelRequest travelRequest){
		ValidationResult accommodation = validation.required(travelRequest.accommodation);
		if(accommodation.error != null){
			validation.addError("travelRequest.accommodation", "&{validation.required}");
		}
		ValidationResult meal = validation.required(travelRequest.meal);
		if(meal.error != null){
			validation.addError("travelRequest.meal", "&{validation.required}");
		}
		ValidationResult stars = validation.required(travelRequest.stars);
		if(stars.error != null){
			validation.addError("travelRequest.stars", "&{validation.required}");
		}
		
	}
	
	@Util
	private static void validateStep5(TravelRequest travelRequest){
		
		if(travelRequest.experiences != null){
			ValidationResult experiences = validation.isTrue(travelRequest.experiences.size() > 0);
			if(experiences.error != null){
				validation.addError("travelRequest.experiences", "&{validation.required}");
			}
		} else {
			validation.addError("travelRequest.experiences", "&{validation.required}");
		}
		ValidationResult descriptionFour = validation.isTrue(travelRequest.descriptionFour.length() <= TravelRequest.MAX_DESCRIPTION_SIZE );
		if(descriptionFour.error != null){
			validation.addError("travelRequest.descriptionFour", "&{validation.required}");
		}
		if(travelRequest.experiences != null){
			if(travelRequest.experiences.size()>0){
				descriptionFour = validation.required(travelRequest.descriptionFour);
				if(descriptionFour.error != null){
					validation.addError("travelRequest.descriptionFour", "&{validation.required}");
				}
			}
		}
		
	}

	@Util
	private static void notifySuitedProviders(TravelRequest travelRequest) {
		LoggerHelper.info_Logger( "notifying suited providers");
		
		List<Provider> providers = Provider.findAll();
		
		for (Provider provider : providers) {
			if(provider.user.enable){
				List<Restriction> restrictions = Restriction.findByProvider(provider);
	
				checkRestrictionsAndSendMail(restrictions, travelRequest, provider);
			}
		}
		
	}
	
	@Util
	private static void checkRestrictionsAndSendMail(List<Restriction> restrictions, TravelRequest travelRequest, Provider provider){
		
		if(restrictions == null || restrictions.isEmpty() ){
			notifyBySource(travelRequest, provider);
			return;
		}
		
		for (Restriction restriction : restrictions) {
			if(restriction.country.equals(travelRequest.country_to)){
				notifyBySource(travelRequest, provider);
				return;
			}
		}
		
		return;
	}
	
	@Util
	private static void notifyBySource(TravelRequest travelRequest, Provider provider){
		
		SourceRestriction source_restriction = SourceRestriction.findByProvider(provider);
		CountryGroup req_group = CountryGroup.findByName(CountryGroup.SUPPORTED);
		
		if(req_group.countries.contains(travelRequest.country_location)){
			if(source_restriction.locations != null){
				if(source_restriction.locations.contains(travelRequest.location)){
					UbeosMailer.notifyProviderNewRequest(provider.user, travelRequest, "New Request");
					
					ReplyStat replyStat = new ReplyStat(travelRequest.id, true, false, provider);
					replyStat.save();
					
					return;
				}
			}
		} else { // is Other
			if(source_restriction.countryGroup.equals(CountryGroup.findByName(CountryGroup.OTHER))){
				UbeosMailer.notifyProviderNewRequest(provider.user, travelRequest, "New Request");
				
				ReplyStat replyStat = new ReplyStat(travelRequest.id, true, false, provider);
				replyStat.save();
				
				return;
			}
		}

		if(source_restriction.countryGroup.equals(CountryGroup.findByName(CountryGroup.ALL))){
			UbeosMailer.notifyProviderNewRequest(provider.user, travelRequest, "New Request");
			
			ReplyStat replyStat = new ReplyStat(travelRequest.id, true, false, provider);
			replyStat.save();
			
			return;
		}
		
		return;
	}
	
	@Util
	private static List<TravelRequest> getFilteredRequests(List<TravelRequest> allRequests, Provider provider){
		
		List<TravelRequest> returningList = new ArrayList<TravelRequest>();
		List<Restriction> restrictions = Restriction.findByProvider(provider);
		//		SourceRestriction source_restriction = SourceRestriction.findByProvider(provider);
		//		CountryGroup req_group = CountryGroup.findByName(CountryGroup.SUPPORTED);
		
		for (TravelRequest travelRequest : allRequests) {
			if(restrictions == null || restrictions.isEmpty() ){
				if(sourceRestrictionMatch(travelRequest, provider)){
					returningList.add(travelRequest);
				}
			}
			
			for (Restriction restriction : restrictions) {
				if(restriction.country.equals(travelRequest.country_to)){
					if(sourceRestrictionMatch(travelRequest, provider)){
						returningList.add(travelRequest);
					}
				}
			}
		}
		
		return returningList;
	}
	
	@Util
	private static boolean sourceRestrictionMatch(TravelRequest travelRequest, Provider provider){
		
		SourceRestriction source_restriction = SourceRestriction.findByProvider(provider);
		CountryGroup req_group = CountryGroup.findByName(CountryGroup.SUPPORTED);
				
		if(UserTypeHelper.isProvider(session)){
			if(req_group.countries.contains(travelRequest.country_location)){
				if(source_restriction.locations.contains(travelRequest.location)){
					return true;
				}
			} else { // is Other
				if(source_restriction.countryGroup.equals(CountryGroup.findByName(CountryGroup.OTHER))){
					UbeosMailer.notifyProviderNewRequest(provider.user, travelRequest, "New Request");
					return true;
				}
			}
			
			if(source_restriction.countryGroup.equals(CountryGroup.findByName(CountryGroup.ALL))){
				return true;
			}
			
			return false;
		} else if(UserTypeHelper.isAdmin(session)){
			return true;
		} else {
			return false;
		}
	}


}
