package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.util.List;

import models.Billing;
import models.Comment;
import models.Consumer;
import models.Country;
import models.Proposal;
import models.ProposalState;
import models.Provider;
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
public class Comments extends MyController {
	
	@RestrictedResource(name = {UserRole.ADMIN, UserRole.PROVIDER})
	public static void save(Comment comment) {
		LoggerHelper.info_Logger("Saving comment");
		
		if(!UserTypeHelper.isProvider(session)){
			LoggerHelper.info_Logger("Trying to comment as user!");
			notFound();
		}
		
		User logged = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(logged);
		
		comment.enable = true;
		comment.company = provider.company;
		
		comment.text = "<b>" + logged.firstName + " " + logged.lastName +":</b> " + comment.text;
		
		comment.save();
		
		TravelRequests.show(comment.request.id);
	}
}	