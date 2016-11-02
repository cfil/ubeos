package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;
import models.Consumer;
import models.ConsumerRating;
import models.Proposal;
import models.ProposalState;
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
public class ConsumerRatings extends MyController {

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void create(Long id, ConsumerRating consumerRating) {
		LoggerHelper.info_Logger("Rating for proposal: "+id);
		
		User user = UserTypeHelper.getLoggedUser(session);
		
		Proposal proposal = Proposal.findById(id);
		if(proposal == null){
			LoggerHelper.info_Logger("Trying to rating unexistent proposal");
			notFound();
		}
		if(user != proposal.provider.user){
			LoggerHelper.info_Logger("Trying to rating other user proposal");
			notFound();
		}
		
		ConsumerRating rating = ConsumerRating.findByProposal(proposal);
		if(rating != null){
			LoggerHelper.info_Logger("Trying to rating unexistent proposal");
			notFound();
		}
			
		if(!proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
			LoggerHelper.info_Logger("Trying to rating travel not maded yet");
			notFound();
		}
		
		consumerRating.proposal = proposal;
		consumerRating.provider = proposal.provider;
		consumerRating.consumer = proposal.request.consumer;
		
		render(consumerRating);
	}

	@RestrictedResource(name = { UserRole.ADMIN, UserRole.PROVIDER })
	public static void save(@Valid ConsumerRating consumerRating) {
		LoggerHelper.info_Logger("Saving feedback");
		
		if(!consumerRating.proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
			LoggerHelper.info_Logger( "Tried to rate when not on feedback");
			notFound();
		}
		
		if (validation.hasErrors()) {
			flash.error(Messages.get("rating.value.required"));
			flash.put(Security.RENDER_ERROR, true);
			LoggerHelper.info_Logger("[Validation Error] - " + validation.errorsMap().toString());
			render("@create", consumerRating);
		}
		
		User user = UserTypeHelper.getLoggedUser(session);
		if(user != consumerRating.proposal.provider.user){
			LoggerHelper.info_Logger("Trying to rating other user proposal");
			notFound();
		}
		
		consumerRating.save();
		
		Proposal proposal = consumerRating.proposal;
		proposal.state = ProposalState.findByName(ProposalState.CLOSED);
		proposal.save();
		
		TravelRequest request = proposal.request;
		request.state = RequestState.findByName(RequestState.CLOSED);
		request.save();
		
		flash.success(Messages.get("views.rated.success", "ConsumerRating"));
		Application.index();
	}

}
