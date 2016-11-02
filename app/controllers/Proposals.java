package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Accommodation;
import models.Activation;
import models.ActivationMotive;
import models.Billing;
import models.Comment;
import models.Consumer;
import models.Country;
import models.Experience;
import models.Meal;
import models.MessagesRec;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.RejectMotive;
import models.RejectStat;
import models.ReplyStat;
import models.RequestState;
import models.Transport;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;

import org.apache.commons.lang.StringEscapeUtils;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation.ValidationResult;
import play.i18n.Messages;
import play.modules.paginate.ModelPaginator;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import utils.StringUtils;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;

@With(Deadbolt.class)
public class Proposals extends MyController {
		
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })	
	public static void index() {
		LoggerHelper.info_Logger( "List Proposals");
		
		User user = User.findById(Long.parseLong(session.get("ubeos_user")));
		isValidUser(user);
		
		List<Proposal> proposals = new ArrayList<Proposal>();
		if(user.userRole.name.equals(UserRole.PROVIDER)){
			Provider provider = Provider.find("byUser_id",session.get("ubeos_user")).first();
			List<Proposal> proposals_tmp = Proposal.find("provider_id=? order by created_at desc", provider.id).fetch();
			for (Proposal proposal : proposals_tmp) {
				if(proposal.state.proposalState.equals(ProposalState.ACTIVE) && proposal.ispublic){
					proposals.add(proposal);
				}
			}
			
		} else if(user.userRole.name.equals(UserRole.ADMIN) ){
			proposals = Proposal.findAll();
		}
		
		String activeTab = "index";
		String activeHeader = "indexProposals";
			
		render(proposals, activeTab, activeHeader);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void ignore(Long request_id) {
		LoggerHelper.info_Logger( "Ignoring request: " + request_id);
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		TravelRequest travelRequest = TravelRequest.findById(request_id);
		isValidRequest(travelRequest);
		
		if(!travelRequest.state.requestState.equals(RequestState.ACTIVE) ){
			LoggerHelper.info_Logger( "Tried to ignore for invalid (deleted or accepted) request: " + request_id);
			notFound();
		}
		
		Proposal proposal = new Proposal();
		
		proposal.isNew = Proposal.READED;
		proposal.request = travelRequest;
		proposal.state = ProposalState.findByName(ProposalState.ACTIVE);
		proposal.dateFrom = travelRequest.dateFrom;
		proposal.dateTo = travelRequest.dateTo;
		proposal.accommodation = travelRequest.accommodation;
		proposal.transport = travelRequest.transport;
		proposal.meal = travelRequest.meal;
//		proposal.experiences = travelRequest.experiences;
		proposal.city_from = travelRequest.city_from;
		proposal.city_to = travelRequest.city_to;
		proposal.nights = travelRequest.nights;
		proposal.price = 1.0;
		proposal.ispublic = false;
		proposal.provider = Provider.findByUser(user);
		
		proposal.save();
		
		TravelRequests.search();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void unignore(Long request_id) {
		LoggerHelper.info_Logger( "Delete Ignore request: " + request_id);
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		TravelRequest travelRequest = TravelRequest.findById(request_id);
		isValidRequest(travelRequest);
		
		if(!travelRequest.state.requestState.equals(RequestState.ACTIVE) ){
			LoggerHelper.info_Logger( "Tried to revert ignore for invalid (deleted or accepted) request: " + request_id);
			notFound();
		}
		
		Provider provider = Provider.findByUser(user);
		Proposal proposal = Proposal.findByRequestAndProviderIgnored(travelRequest, provider);
		
		proposal.delete();
		
		TravelRequests.search();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void create(Long request_id, Proposal proposal) {
		LoggerHelper.info_Logger( "Creating proposal for request: " + request_id);
		
		TravelRequest travelRequest = TravelRequest.findById(request_id);
		isValidRequest(travelRequest);
		
		if(!travelRequest.state.requestState.equals(RequestState.ACTIVE) ){
			LoggerHelper.info_Logger( "Tried to create proposal for invalid (deleted or accepted) request: " + request_id);
			notFound();
		}
		
		List<Comment> c_comments = new ArrayList<Comment>();
		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		c_comments = Comment.getCommentByCompanyAndRequest(provider.company, travelRequest);
		
		proposal.request = travelRequest;
		proposal.state = ProposalState.findByName(ProposalState.ACTIVE);
		proposal.dateFrom = travelRequest.dateFrom;
		proposal.dateTo = travelRequest.dateTo;
		proposal.accommodation = travelRequest.accommodation;
		proposal.transport = travelRequest.transport;
		proposal.meal = travelRequest.meal;
		proposal.experiences = travelRequest.experiences;
		proposal.city_from = travelRequest.city_from;
		proposal.city_to = travelRequest.city_to;
		proposal.nights = travelRequest.nights;
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		proposal.date_from = dateFormat.format(proposal.dateFrom);
		proposal.date_to = dateFormat.format(proposal.dateTo);
		
		List<Accommodation> accommodations = Accommodation.findAll();
		List<Transport> transports = Transport.findAll();
		List<Meal> meals = Meal.findAll();
		List<Experience> experiences = Experience.findAll();
		
		render(request_id,travelRequest, proposal, accommodations, transports, meals, experiences, c_comments);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void edit(Long proposal_id) {
		LoggerHelper.info_Logger( "Editing proposal : " + proposal_id);
		
		Proposal proposal = Proposal.findById(proposal_id);
		if(proposal == null){
			LoggerHelper.info_Logger( "Tried to edit unexistent proposal");
			notFound();
		}
		
		isValidRequest(proposal.request);
		
		if(!proposal.request.state.requestState.equals(RequestState.ACTIVE) ){
			LoggerHelper.info_Logger( "Tried to edit proposal for invalid (deleted or accepted) request: " + proposal_id);
			notFound();
		}
	
		List<Comment> c_comments = new ArrayList<Comment>();
		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		c_comments = Comment.getCommentByCompanyAndRequest(provider.company, proposal.request);
		
		List<Accommodation> accommodations = Accommodation.findAll();
		List<Transport> transports = Transport.findAll();
		List<Meal> meals = Meal.findAll();
		List<Experience> experiences = Experience.findAll();
		
		TravelRequest travelRequest = proposal.request;
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		proposal.date_from = dateFormat.format(proposal.dateFrom);
		proposal.date_to = dateFormat.format(proposal.dateTo);
		
		render("@create", travelRequest, proposal, accommodations, transports, meals, experiences, c_comments);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER, UserRole.CONSUMER })
	public static void show(Long id) {
		LoggerHelper.info_Logger( "View proposal " + id);
		
		Proposal proposal = Proposal.findById(id);
		isValidProposal(proposal);
		
		User user = UserTypeHelper.getLoggedUser(session);
		
		if(user.userRole.name.equals(UserRole.PROVIDER)){
			// DO NOTHING
		} else if(user.userRole.name.equals(UserRole.CONSUMER) ){
			if(proposal.request.consumer.user != user){
				LoggerHelper.info_Logger("Tried to view other user proposal");
				notFound();
			}
			
			if (proposal.isNew == Proposal.ISNEW || proposal.isNew == Proposal.EDITED) {
				proposal.isNew = Proposal.READED;
				proposal.save();
			}
		}
				
		List<Country> countries = Country.findAll();
		List<Accommodation> accommodations = Accommodation.findAll();
		List<Transport> transports = Transport.findAll();
		List<Meal> meals = Meal.findAll();
		List<Experience> experiences = Experience.findAll();
				
		boolean inBudget = isInBudget(proposal.request, proposal.price);
		TravelRequest travelRequest = proposal.request;
		Provider provider = proposal.provider;
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		proposal.date_from = dateFormat.format(proposal.dateFrom);
		proposal.date_to = dateFormat.format(proposal.dateTo);
		
		Billing billing = Billing.findByProposal(proposal);
		
		render(proposal, countries, accommodations, transports, meals, experiences, inBudget, travelRequest, provider, billing);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void delete(Long id) {
		LoggerHelper.info_Logger( "Delete proposal " + id);
		Proposal proposal =  Proposal.findById(id);
		isValidProposal(proposal);
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user.userRole.name.equals(UserRole.PROVIDER)){
			Provider provider = proposal.provider;
			if(user != provider.user){
				LoggerHelper.info_Logger( "Trying to delete other user proposal");
				notFound();
			}
						
		} 
		
		if(proposal.state != ProposalState.findByName(ProposalState.ACTIVE)){
			LoggerHelper.info_Logger( "Trying to delete proposal non active");
			notFound();
		}
		
		proposal.state = ProposalState.findByName(ProposalState.DISABLED);
		proposal.save();

		index();
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void save(@Valid Proposal proposal) {
		LoggerHelper.info_Logger( "Saving proposal");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			proposal.dateFrom = new Date(formatter.parse(proposal.date_from).getTime());
			proposal.dateTo = new Date(formatter.parse(proposal.date_to).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(!proposal.request.state.requestState.equals(RequestState.ACTIVE) || !proposal.request.enable){
			LoggerHelper.info_Logger( "Tried to create proposal for invalid (deleted or accepted) request: " + proposal.request.id);
			notFound();
		}
		TravelRequest travelRequest = TravelRequest.findById(proposal.request.id);

		boolean contactsError = false;
		if(!StringUtils.isNullOrEmpty(proposal.description)){
			ValidationResult description = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.description));
			ValidationResult description_size = validation.isTrue(proposal.description.length() <= Proposal.MAX_DESCRIPTION_SIZE );
			if(description.error != null || description_size.error != null){
				validation.addError("proposal.description", "&{scaffold.validation.contacts}");
				contactsError = true;
			}
		}
		if(!StringUtils.isNullOrEmpty(proposal.descriptionTwo)){
			ValidationResult descriptionTwo = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionTwo));
			ValidationResult descriptionTwo_size = validation.isTrue(proposal.descriptionTwo.length() <= Proposal.MAX_DESCRIPTION_SIZE );
			if(descriptionTwo.error != null || descriptionTwo_size.error != null){
				validation.addError("proposal.descriptionTwo", "&{scaffold.validation.contacts}");
				contactsError = true;
			}
		}
		if(!StringUtils.isNullOrEmpty(proposal.descriptionThree)){
			ValidationResult descriptionThree = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionThree));
			ValidationResult descriptionThree_size = validation.isTrue(proposal.descriptionThree.length() <= Proposal.MAX_DESCRIPTION_SIZE );
			if(descriptionThree.error != null || descriptionThree_size.error != null){
				validation.addError("proposal.descriptionThree", "&{scaffold.validation.contacts}");
				contactsError = true;
			}
		}
		if(!StringUtils.isNullOrEmpty(proposal.descriptionFour)){
			ValidationResult descriptionFour = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionFour));
			ValidationResult descriptionFour_size = validation.isTrue(proposal.descriptionFour.length() <= Proposal.MAX_DESCRIPTION_SIZE );
			if(descriptionFour.error != null || descriptionFour_size.error != null){
				validation.addError("proposal.descriptionFour", "&{scaffold.validation.contacts}");
				contactsError = true;
			}
		}
		
		boolean price_error = false;
		ValidationResult price_val = validation.min("proposal.price", proposal.price, 11.0);
		if(!price_val.ok){
			price_error = true;
		}
			
		if(travelRequest.wantAccommodation){
			validation.required("proposal.descriptionThree",proposal.descriptionThree);
		}
		if(travelRequest.wantTransportation){
			validation.required("proposal.description",proposal.description);
		}
		if(travelRequest.wantActivities){
			validation.required("proposal.descriptionFour",proposal.descriptionFour);
		}
		if (validation.hasErrors()) {
			
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			
			if(price_error){
				flash.error(Messages.get("validarion.price.min"));
			}
			
			List<Country> countries = Country.findAll();

			List<Accommodation> accommodations = Accommodation.findAll();
			List<Transport> transports = Transport.findAll();
			List<Meal> meals = Meal.findAll();
			List<Experience> experiences = Experience.findAll();
			
			List<Comment> c_comments = new ArrayList<Comment>();
			User user = UserTypeHelper.getLoggedUser(session);
			Provider provider = Provider.findByUser(user);
			c_comments = Comment.getCommentByCompanyAndRequest(provider.company, travelRequest);
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@create", travelRequest, proposal.request.id ,proposal, countries, accommodations, transports, meals, experiences, c_comments);
		}
		
		proposal.state = ProposalState.findByName(ProposalState.ACTIVE);
		
		if(StringUtils.isNullOrEmpty(proposal.title)){
			
			proposal.title = "REF_"+proposal.provider.proposals.size(); 
		}
		
		proposal.save();
		
		UbeosMailer.notifyConsumerNewProposal(proposal.request.consumer.user, proposal);
		
		utils.CompetitionUtils.competitionSystem(proposal);
		
		ReplyStat replyStat = ReplyStat.findByRequestIDandProvider(travelRequest.id, proposal.provider);
		if(replyStat != null){
			if(!replyStat.replied){
				replyStat.replied = true;
				replyStat.save();
			}
		}
		
		flash.success(Messages.get("scaffold.created", "Proposal"));
		index();
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void update(@Valid Proposal proposal) {
		LoggerHelper.info_Logger( "Updating proposal");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			proposal.dateFrom = new Date(formatter.parse(proposal.date_from).getTime());
			proposal.dateTo = new Date(formatter.parse(proposal.date_to).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(!proposal.request.state.requestState.equals(RequestState.ACTIVE) || !proposal.request.enable){
			LoggerHelper.info_Logger( "Tried to create proposal for invalid (deleted or accepted) request: " + proposal.request.id);
			notFound();
		}
		TravelRequest travelRequest = TravelRequest.findById(proposal.request.id);

		boolean contactsError = false;
		ValidationResult description = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.description));
		ValidationResult description_size = validation.isTrue(proposal.description.length() <= Proposal.MAX_DESCRIPTION_SIZE );
		if(description.error != null || description_size != null){
			validation.addError("proposal.description", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		ValidationResult descriptionTwo = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionTwo));
		ValidationResult descriptionTwo_size = validation.isTrue(proposal.descriptionTwo.length() <= Proposal.MAX_DESCRIPTION_SIZE );
		if(descriptionTwo.error != null || descriptionTwo_size != null){
			validation.addError("proposal.descriptionTwo", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		ValidationResult descriptionThree = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionThree));
		ValidationResult descriptionThree_size = validation.isTrue(proposal.descriptionThree.length() <= Proposal.MAX_DESCRIPTION_SIZE );
		if(descriptionThree.error != null || descriptionThree_size != null){
			validation.addError("proposal.descriptionThree", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		ValidationResult descriptionFour = validation.isTrue(utils.CommUtils.checkValidMessage(proposal.descriptionFour));
		ValidationResult descriptionFour_size = validation.isTrue(proposal.descriptionFour.length() <= Proposal.MAX_DESCRIPTION_SIZE );
		if(descriptionFour.error != null || descriptionFour_size != null){
			validation.addError("proposal.descriptionFour", "&{scaffold.validation.contacts}");
			contactsError = true;
		}
		
		validation.min("proposal.price", proposal.price, 1.0);
		if (validation.hasErrors()) {
			if(contactsError){
				flash.error(Messages.get("scaffold.validation.required")+"<br>"+Messages.get("scaffold.validation.contacts"));
			} else {
				flash.error(Messages.get("scaffold.validation.required"));
			}
			List<Country> countries = Country.findAll();

			List<Accommodation> accommodations = Accommodation.findAll();
			List<Transport> transports = Transport.findAll();
			List<Meal> meals = Meal.findAll();
			List<Experience> experiences = Experience.findAll();
			
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@create", travelRequest, proposal.request.id ,proposal, countries, accommodations, transports, meals, experiences);
		}
		
		proposal.state = ProposalState.findByName(ProposalState.ACTIVE);
		proposal.isNew = Proposal.EDITED;
		
		if(!StringUtils.isNullOrEmpty(proposal.description)){
			proposal.description = proposal.description.replaceAll("\n", "<br>");
		}
		if(!StringUtils.isNullOrEmpty(proposal.descriptionTwo)){
			proposal.descriptionTwo = proposal.descriptionTwo.replaceAll("\n", "<br>");
		}
		if(!StringUtils.isNullOrEmpty(proposal.descriptionThree)){
			proposal.descriptionThree = proposal.descriptionThree.replaceAll("\n", "<br>");
		}
		if(!StringUtils.isNullOrEmpty( proposal.descriptionFour)){
			proposal.descriptionFour = proposal.descriptionFour.replaceAll("\n", "<br>");
		}
		
		proposal.merge();
		proposal.save();
		
		UbeosMailer.notifyConsumerEditedProposal(proposal.request.consumer.user, proposal);
		
		utils.CompetitionUtils.competitionSystem(proposal);
		
		flash.success(Messages.get("scaffold.edited", "Proposal"));
		index();
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void disable(Long id) {
		LoggerHelper.info_Logger( "Disable proposal " + id);
		Proposal proposal = Proposal.findById(id);
		isValidProposal(proposal);
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user.userRole.name.equals(UserRole.PROVIDER)){
			Provider provider = proposal.provider;
			if(user != provider.user){
				LoggerHelper.info_Logger( "Trying to disable other user proposal");
				notFound();
			}			
		} 
		
		if(proposal.state.proposalState.equals(ProposalState.ACTIVE)){
			proposal.state = ProposalState.findByName(ProposalState.DISABLED);
			proposal.save();
			index();
		} else {
			LoggerHelper.info_Logger( "Trying to disable an non active proposal");
			notFound();
		}
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void enable(Long id) {
		LoggerHelper.info_Logger( "Enable proposal "+ id);
		Proposal proposal = Proposal.findById(id);
		isValidProposal(proposal);
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		if(user.userRole.name.equals(UserRole.PROVIDER)){
			Provider provider = proposal.provider;
			if(user != provider.user){
				LoggerHelper.info_Logger( "Trying to disable other user proposal");
				notFound();
			}
		}
		
		if(proposal.state.proposalState.equals(ProposalState.DISABLED)){
			proposal.state = ProposalState.findByName(ProposalState.ACTIVE);
			proposal.save();
			index();
		} else {
			LoggerHelper.info_Logger( "Trying to enable an active proposal");
			notFound();
		}
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void requestProposals(Long id) {
		LoggerHelper.info_Logger( "Show proposals for request " + id);
		
		TravelRequest travelRequest = TravelRequest.findById(id);
		isValidRequest(travelRequest);
		
		User user = User.findById(Long.parseLong(session.get("ubeos_user")));
		isValidUser(user);
		
		Consumer consumer = Consumer.find("byUser", user).first();
		
		if(user.userRole.name.equals(UserRole.CONSUMER) && !travelRequest.consumer.equals(consumer)){
			LoggerHelper.info_Logger( "Trying to show proposals of other user request");
			notFound();
		}
		
		List<Proposal> proposals = Proposal.find("byRequest", travelRequest).fetch();			
		ValuePaginator paginator = new ValuePaginator(proposals);
		paginator.setPageSize(10);
		
		renderTemplate("Proposals/index.html", paginator);
	}
		
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void accept(Long id) {
		LoggerHelper.info_Logger( "Accept proposal " + id);
				
		Proposal proposal = Proposal.findById(id);
		isValidProposal(proposal);
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		if(user.userRole.name.equals(UserRole.CONSUMER) ){
			Consumer consumer = proposal.request.consumer;
			if(user != consumer.user){
				LoggerHelper.info_Logger( "Trying to accept proposal of other consumer request");	
				notFound();
			}
		}

		if(proposal.state != ProposalState.findByName(ProposalState.ACTIVE)){
			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
			notFound();
		}
		
		if(proposal.request.state != RequestState.findByName(RequestState.ACTIVE) && proposal.request.state != RequestState.findByName(RequestState.ACCEPTED)){
			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
			notFound();
		}
		
		Billings.create(proposal.id, null);
		
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void history() {
		LoggerHelper.info_Logger( "Historic");
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		List<Proposal> proposals = new ArrayList<Proposal>();
		if(!user.userRole.name.equals(UserRole.PROVIDER) && !user.userRole.name.equals(UserRole.ADMIN)){
			LoggerHelper.info_Logger( "Trying to access other consumer history");
			notFound();
		}
			
		Provider provider = Provider.findByUser(user);
//		proposals = Proposal.find("byProviderAndState", provider, ProposalState.findByName(ProposalState.CLOSED)).fetch();			
		proposals = Proposal.findByProviderCompanyAndState(provider, ProposalState.findByName(ProposalState.CLOSED));
				
		String activeTab = "history";
		String activeHeader = "indexProposals";
		
		renderTemplate("Proposals/index.html",proposals, activeTab, activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void accepted() {
		LoggerHelper.info_Logger( "Show accepted proposals");
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		List<Proposal> proposals = new ArrayList<Proposal>();
		if(!user.userRole.name.equals(UserRole.PROVIDER) && !user.userRole.name.equals(UserRole.ADMIN)){
			LoggerHelper.info_Logger( "Trying to show accepted proposals of other provider");
			notFound();
		}
			
		Provider provider = Provider.findByUser(user);

//		List<Proposal> proposalsProvider = Proposal.find("provider_id = ? order by created_at desc",provider).fetch();
		List<Proposal> proposalsProvider = Proposal.findByProviderCompany(provider);
		for (Proposal proposal : proposalsProvider) {
			if(proposal.state.proposalState.equals(ProposalState.ACCEPTED) || proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
				if(!proposal.state.proposalState.equals(ProposalState.DISABLED)){
					proposals.add(proposal);
				}
			}
		}
		
		
		String activeTab = "accepted";
		String activeHeader = "indexProposals";
		

		renderTemplate("Proposals/index.html",proposals, activeTab, activeHeader);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void ignored() {
		LoggerHelper.info_Logger( "Show ignored proposals (Requests)");
		
		User user = UserTypeHelper.getLoggedUser(session);
		isValidUser(user);
		
		if(!user.userRole.name.equals(UserRole.PROVIDER) && !user.userRole.name.equals(UserRole.ADMIN)){
			LoggerHelper.info_Logger( "Trying to show accepted proposals of other provider");
			notFound();
		}
			
		Provider provider = Provider.findByUser(user);
		

		List<TravelRequest> requests = new ArrayList<TravelRequest>();
//		List<Proposal> proposalsProvider = Proposal.find("provider_id = ? order by created_at desc",provider).fetch();
		List<Proposal> proposalsProvider = Proposal.findByProviderCompany(provider);
		for (Proposal proposal : proposalsProvider) {
			if(!proposal.ispublic ){
				if(proposal.request.state == RequestState.findByName(RequestState.ACTIVE)){
					if(!proposal.state.proposalState.equals(ProposalState.DISABLED) && !proposal.ispublic){
//						proposals.add(proposal);
						requests.add(proposal.request);
					}
				}
			}
		}
				
		String activeTab = "ignored";
		String activeHeader = "indexProposals";
		
		renderTemplate("TravelRequests/search.html",requests, activeTab, activeHeader);
	}
	
	
	// ~~~~~~~~~~~~~
	
	@Util
	public static void rejectProposalAndNotify(Proposal proposal, Long motive_id, String message){
		if(proposal.state == ProposalState.findByName(ProposalState.ACTIVE)){
			proposal.state = ProposalState.findByName(ProposalState.REJECTED);
			proposal.save();
			UbeosMailer.proposalRejected(proposal.provider.user,proposal, "rejected proposal " + proposal.id, motive_id,message);
			RejectStat stat = new RejectStat(proposal.request.consumer.user.id, proposal.provider.user.id, proposal.request.id, proposal.id, motive_id, message);
			stat.save();
		}
	}
	
	@Util
	public static void deleteRejectProposalAndNotify(Proposal proposal, Long motive_id, String message){
		if(proposal.state.proposalState.equals(ProposalState.ACTIVE)){
			proposal.state = ProposalState.findByName(ProposalState.REJECTED);
			proposal.save();
			UbeosMailer.proposalRejectedByDelete(proposal.provider.user,proposal, "rejected proposal " + proposal.id, motive_id,message);
			RejectStat stat = new RejectStat(proposal.request.consumer.user.id, proposal.provider.user.id, proposal.request.id, proposal.id, RejectMotive.findByName(RejectMotive.DELETED).id, message);
			stat.save();
		}
	}
	
	@Util
	public static void expiredCancelProposalAndNotify(Proposal proposal, Long motive_id){
		if(proposal.state.proposalState.equals(ProposalState.ACTIVE)){
			proposal.state = ProposalState.findByName(ProposalState.CANCELED);
			proposal.save();
			UbeosMailer.proposalCancelByExpired(proposal.provider.user,proposal, "proposal " + proposal.id + " canceled", motive_id);
			RejectStat stat = new RejectStat(proposal.request.consumer.user.id, proposal.provider.user.id, proposal.request.id, proposal.id, RejectMotive.findByName(RejectMotive.DELETED).id, "EXPIRED");
			stat.save();
		}
	}
	
	@Util
	private static boolean isInBudget(TravelRequest request, double price) {
		
		if(price <= request.maxBudget )
			return true;
		
		return false;
	}


}