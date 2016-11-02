package controllers;

import java.util.List;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.RestrictedResource;

import models.Proposal;
import models.TravelRequest;
import models.UserRole;
import play.mvc.Controller;
import play.mvc.With;

@With(Deadbolt.class)
public class FixDescriptions extends MyController {

	@RestrictedResource(name = { UserRole.ADMIN })
    public static void index() {
        
    	List<Proposal> proposals = Proposal.findAll();
    	for (Proposal proposal : proposals) {
    		if(proposal.description != null){
    			proposal.description = proposal.description.replaceAll("\n", "<br>");
    		}
    		if(proposal.descriptionTwo != null){
    			proposal.descriptionTwo = proposal.descriptionTwo.replaceAll("\n", "<br>");
    		}
    		if(proposal.descriptionThree != null){
    			proposal.descriptionThree = proposal.descriptionThree.replaceAll("\n", "<br>");
    		}
    		if(proposal.descriptionFour != null){
    			proposal.descriptionFour = proposal.descriptionFour.replaceAll("\n", "<br>");
    		}
    		proposal.save();
		}
    	
    	List<TravelRequest> requests = TravelRequest.findAll();
    	for (TravelRequest travelRequest : requests) {
    		if(travelRequest.description != null){
    			travelRequest.description = travelRequest.description.replaceAll("\n", "<br>");
    		}
    		if(travelRequest.descriptionTwo != null){
    			travelRequest.descriptionTwo = travelRequest.descriptionTwo.replaceAll("\n", "<br>");
    		}
    		if(travelRequest.descriptionThree != null){
    			travelRequest.descriptionThree = travelRequest.descriptionThree.replaceAll("\n", "<br>");
    		}
    		if(travelRequest.descriptionFour != null){
    			travelRequest.descriptionFour = travelRequest.descriptionFour.replaceAll("\n", "<br>");
    		}
    		travelRequest.save();
		}
    	Application.index();
    }

}
