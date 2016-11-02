package jobs;


import java.util.Date;
import java.util.List;
import java.util.Set;

import controllers.Proposals;

import models.Proposal;
import models.ProposalState;
import models.RejectMotive;
import models.RequestState;
import models.TravelRequest;
import notifiers.UbeosMailer;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;


@On("cron.time.close.travel")
public class TravelCloser extends Job {

	
	/*
	 * This Job runs with 'cron.time.close.travel' interval (check application.conf file) and change the state
	 * of requests and proposals to FEEDBACK, sending emails for consumer and provider to rate each other if the travel was made
	 */
	@Override
	public void doJob() throws Exception {
		Logger.info("[ *** JOB: TRAVEL CLOSER *** ] - Start");
		int closedTravel = 0;
		int deletedTravel = 0;
		
		// FOR OLD REQUESTS AND period of acceptante bigger than travel date
		// Get all active requests
		List<TravelRequest> travelRequests = TravelRequest.findByState(RequestState.findByName(RequestState.ACTIVE));
		for (TravelRequest travelRequest : travelRequests) {
			
			
			if(travelRequest.dateFrom.before(new Date())) {
				travelRequest.state = RequestState.findByName(RequestState.CLOSED);
				travelRequest.save();
				Set<Proposal> proposals = travelRequest.proposals;
				for (Proposal prop : proposals) {
					RejectMotive motive = RejectMotive.findByName(RejectMotive.EXPIRED);
					Proposals.expiredCancelProposalAndNotify(prop,motive.id);
				}
				
				//TODO: SEND MAIL TO CONSUMER
				deletedTravel++;
			}
		}
		// END FOR OLD REQUESTS
		
		// Get all pending proposals (accepted)
		List<Proposal> proposals = Proposal.getPendingProposals();
		// Check each...
		for (Proposal proposal : proposals) {
			// If proposal is accepted than it was made
			if(proposal.state.equals(ProposalState.findByName(ProposalState.ACCEPTED))){
				// If dateTo is before today, it means that it is over or no longer valid
				if(proposal.dateTo.before(new Date())){
					proposal.state = ProposalState.findByName(ProposalState.FEEDBACK);
					proposal.save();
					TravelRequest request = proposal.request;
					request.state = RequestState.findByName(RequestState.FEEDBACK);
					request.save();
					
					UbeosMailer.requestProviderFeedback(proposal);
					UbeosMailer.requestConsumerFeedback(proposal);
					closedTravel++;
					
				}
			} else {
				// TODO: close invalid requests and proposals...
			}
		}
		Logger.info("[ *** JOB: TRAVEL CLOSER *** ] - travels deleted  : " + deletedTravel );
		Logger.info("[ *** JOB: TRAVEL CLOSER *** ] - travels closed   : " + closedTravel );
		Logger.info("[ *** JOB: TRAVEL CLOSER *** ] - End");
	}
}