package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.util.Date;

import models.Proposal;
import models.ProposalState;
import models.TravelRequest;
import models.User;
import notifiers.UbeosMailer;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.Play;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;

@With(Deadbolt.class)
public class MyController extends Controller {
	
	/**
	 * Method to send email to ubeos@ubeos.com when an Error Occurs
	 * 
	 * @param throwable
	 */
	@Catch(Throwable.class)
    public static void sendError(Throwable throwable) {
		LoggerHelper.error_Logger("Sending Mail With Error");
		
		// Get Error StackTrace
		String error = ExceptionUtils.getStackTrace(throwable);

		// Get User that generates the error
		User user = helpers.UserTypeHelper.getLoggedUser(session);
		
		// Get Date and Time of the error
		Date date = new Date();
      
		// send mail...
		if(Play.id.equals("prod")){
			UbeosMailer.sendError(user, error, date);
			LoggerHelper.error_Logger("Mail Sent");
		}
    }
	
	
	// *************************** UTIL METHODS ***************************
	
	@Util
	protected static void isValidUser(User user){
		if(user==null){
			LoggerHelper.info_Logger( "Tried to access protected area without login");
			notFound();
		}
	}
	
	@Util
	protected static void isValidRequest(TravelRequest travelRequest){
		
		if(travelRequest == null){
			LoggerHelper.info_Logger("Tried to access unexistent request");
			notFound();
		}
		
		if(!travelRequest.enable){
			LoggerHelper.info_Logger("Tried to access deleted request");
			TravelRequests.requestDeleted();
		}	
	}
	
	@Util
	protected static void isValidProposal(Proposal proposal){
		
		if(proposal == null){
			LoggerHelper.info_Logger("Tried to access unexistent proposal");
			notFound();
		}
		
		if(proposal.state.equals(ProposalState.DISABLED)){
			LoggerHelper.info_Logger("Tried to access deleted request");
			TravelRequests.requestDeleted();
		}
		
	}
	
	@Util
	protected static void isUserLoggedIn(){
		if(UserTypeHelper.getLoggedUser(session) != null){
			Application.index();
		}
	}
}