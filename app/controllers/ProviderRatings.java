package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.ProviderRating;
import models.RequestState;
import models.TravelRequest;
import models.User;
import models.UserRole;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;

@With(Deadbolt.class)
public class ProviderRatings extends MyController {

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void create(Long id, ProviderRating providerRating) {
		Logger.debug("[ProviderRatings.create]");
		LoggerHelper.info_Logger( "Rating proposal id: " + id);
		
		User user = UserTypeHelper.getLoggedUser(session);
		
		Proposal proposal = Proposal.findById(id);	
		if(proposal == null){
			LoggerHelper.info_Logger( "Tried to rate unexistent proposal");
			notFound();
		}
		
		if(user != proposal.request.consumer.user){
			LoggerHelper.info_Logger( "Tried to rate unexistent user");
			notFound();
		}

		
		ProviderRating rating = ProviderRating.findByProposal(proposal);
		if(rating != null){
			LoggerHelper.info_Logger( "Tried to rate when not possible");
			notFound();
		}
		
		
		if(!proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
			LoggerHelper.info_Logger( "Tried to rate when not on feedback yet");
			notFound();
		}
		
		providerRating.proposal = proposal;
		providerRating.provider = proposal.provider;
		providerRating.consumer = proposal.request.consumer;
		
		render(providerRating);
	}
	
	@RestrictedResource(name = { UserRole.ADMIN, UserRole.CONSUMER })
	public static void save(@Valid ProviderRating providerRating) {
		LoggerHelper.info_Logger( "Saving rating");
		
		if(!providerRating.proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
			LoggerHelper.info_Logger( "Tried to rate when not on feedback");
			notFound();
		}
		
		if (validation.hasErrors()) {
			flash.error(Messages.get("rating.value.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger( validation.errorsMap().toString());
			render("@create", providerRating);
		}
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user != providerRating.proposal.request.consumer.user){
			LoggerHelper.info_Logger( "Tried to rate unexistent user");
			notFound();
		}
		
		providerRating.save();
		
		Proposal proposal = providerRating.proposal;
		proposal.state = ProposalState.findByName(ProposalState.CLOSED);
		proposal.save();
		
		TravelRequest request = proposal.request;
		request.state = RequestState.findByName(RequestState.CLOSED);
		request.save();
		
		flash.success(Messages.get("scaffold.created", "ProviderRating"));
		Application.index();
	}

}
