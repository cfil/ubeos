package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.util.List;

import models.Billing;
import models.Consumer;
import models.Country;
import models.Proposal;
import models.ProposalState;
import models.RejectMotive;
import models.RequestState;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.data.validation.Validation.ValidationResult;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;


@With(Deadbolt.class)
public class Billings extends MyController {

	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void create(Long id, Billing billing) {
		LoggerHelper.info_Logger("Billing for proposal: "+id);
		
		Proposal proposal = Proposal.findById(id);
		User user = proposal.request.consumer.user;
		Consumer consumer = Consumer.findByUser(user);
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(user != logged){
			LoggerHelper.info_Logger("Trying to access billing of other user!");
			notFound();
		}
				
//		if(!proposal.state.proposalState.equals(ProposalState.ACTIVE) || !proposal.request.state.requestState.equals(RequestState.ACCEPTED)){
//			LoggerHelper.info_Logger("Trying to access billing of already filled bill!");
//			notFound();
//		}
//		
//		if(proposal.state != ProposalState.findByName(ProposalState.ACTIVE)){
//			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
//			notFound();
//		}
//		
//		if(proposal.request.state != RequestState.findByName(RequestState.ACTIVE) && proposal.request.state != RequestState.findByName(RequestState.ACCEPTED)){
//			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
//			notFound();
//		}
		
		List<Country> countries = Country.findAll();
		
		billing = initializeBilling(billing, user, proposal);
	
		render(id, billing, user, consumer, countries);
	}

	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.CONSUMER})
	public static void save(Long id, Billing billing, User user, Consumer consumer) {
		LoggerHelper.info_Logger("Saving billing for proposal: "+id);
		
		User logged = UserTypeHelper.getLoggedUser(session);
		if(user != logged){
			LoggerHelper.info_Logger("Trying to save billing of other user!");
			notFound();
		}
		
		Proposal proposal = Proposal.findById(id);
		if(proposal.state != ProposalState.findByName(ProposalState.ACTIVE)){
			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
			notFound();
		}
		billing.proposal = proposal;
		
//		if(billing.proposal.request.state != RequestState.findByName(RequestState.ACTIVE) && billing.proposal.request.state != RequestState.findByName(RequestState.ACCEPTED)){
//			LoggerHelper.info_Logger( "Trying to accept proposal when not possible");	
//			notFound();
//		}
		
		
			ValidationResult firstName = validation.required(user.firstName);
			if(firstName.error != null){
				validation.addError("user.firstName", "&{validation.required}");
			}
			ValidationResult lastName = validation.required(user.lastName);
			if(lastName.error != null){
				validation.addError("user.lastName", "&{validation.required}");
			}
			ValidationResult email = validation.required(user.email);
			if(email.error != null){
				validation.addError("user.email", "&{validation.required}");
			}
			ValidationResult address = validation.required(user.address);
			if(address.error != null){
				validation.addError("user.address", "&{validation.required}");
			}
			ValidationResult city = validation.required(user.city);
			if(city.error != null){
				validation.addError("user.city", "&{validation.required}");
			}
			ValidationResult zipcode = validation.required(user.zipcode);
			if(zipcode.error != null){
				validation.addError("user.zipcode", "&{validation.required}");
			}
			ValidationResult country = validation.required(user.country);
			if(country.error != null){
				validation.addError("user.country", "&{validation.required}");
			}
			ValidationResult phone = validation.required(user.phone);
			if(phone.error != null){
				validation.addError("user.phone", "&{validation.required}");
			}
			if (validation.hasErrors()) {
				LoggerHelper.info_Logger("[Validation Error] - " +validation.errorsMap().toString());
				flash.error(Messages.get("scaffold.validation.required"));
				List<Country> countries = Country.findAll();
				flash.put(Security.RENDER_ERROR, true);
				render("@create",id, billing, user, countries);
			}
			
			
			billing = initializeBilling(billing, user, proposal);

			user.merge();
			user.save();
		
		
		billing.save();			
		
		
		if(proposal.state.proposalState.equals(ProposalState.ACTIVE)){

			proposal.state = ProposalState.findByName(ProposalState.ACCEPTED);
			proposal.save();
			UbeosMailer.proposalAccepted(proposal.provider.user,proposal,billing, "accepted proposal " + proposal.id);
			
			TravelRequest travelRequest = TravelRequest.findById(proposal.request.id);
			travelRequest.state = RequestState.findByName(RequestState.PENDING);
			travelRequest.save();
			
			List<Proposal> proposals = Proposal.find("byRequest", travelRequest).fetch();
			for (Proposal prop : proposals) {
				RejectMotive motive = RejectMotive.findByName(RejectMotive.ACCEPTED_OTHER);
				Proposals.rejectProposalAndNotify(prop, motive.id, null);
			}
		}
		
		UbeosMailer.paymentInstructionsPending(billing);
		UbeosMailer.notifyAdminAccepted(proposal);
		
		TravelRequest request = billing.proposal.request;
		request.state = RequestState.findByName(RequestState.PENDING);

		request.save();
		
		flash.success(Messages.get("views.proposal.accepted", "Billing"));
		TravelRequests.index();
	}
	
	/* ******************************************************************************
	 * ******************************* AUXILIARES *******************************
	 * ****************************************************************************** */

	
	@Util
	private static Billing initializeBilling(Billing billing, User user, Proposal proposal){
		billing.name = user.firstName + " " + user.lastName;
		billing.address = user.address;
		billing.city = user.city;
		billing.zipcode = user.zipcode;
		billing.country = user.country;
		billing.proposal = proposal;
		billing.phone = user.phone;
		return billing;
	}
	
}
