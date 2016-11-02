package jobs;

import helpers.ProposalHelper;
import helpers.RequestHelper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Activation;
import models.ActivationMotive;
import models.Company;
import models.Consumer;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.RejectMotive;
import models.RequestState;
import models.SourceRestriction;
import models.TravelRequest;
import models.User;
import models.UserRole;
import notifiers.UbeosMailer;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import controllers.Proposals;


@On("cron.time.user.clean")
public class UserClean extends Job {

	/*
	 * Time passed to delete pending activations
	 * value in millis (3600000 = 1h)
	 */
	private static long TIME_TO_DELETE = 3600000;

	/*
	 * Time passed to accept or reject offer
	 * value in millis (86400000 = 48h) (259200000 = 72h
	 */
//	private final long ACCEPTANCE_PERIOD = 259200000L;
	
	
	/*
	 * This Job runs with 'cron.time.user.clean' interval (check application.conf file) and delete
	 * users that were created but never activated
	 */
	@Override
	public void doJob() throws Exception {
	Logger.info("[ *** JOB: USER CLEAN *** ] - Start");
	int cleanUser = 0;
	int cleanPassRec = 0;
		List<Activation> activations = Activation.findAll();
		Logger.info("[ *** JOB: USER CLEAN *** ] - Activations pending: " + activations.size());
		for (Activation activation : activations) {
			long created = activation.created_at.getTime();
			long now = new Date().getTime();
			long diff = now - created;
			
			if(diff>TIME_TO_DELETE){
				if(activation.motive.equals(ActivationMotive.findByMotive(ActivationMotive.ACTIVATION))){
				long user_id = activation.user;
				
				User user = User.findById(user_id);
					if(user.userRole.name.equals(UserRole.CONSUMER)){
						Consumer consumer = Consumer.findByUser(user);
						consumer.delete();
						
					} else if (user.userRole.name.equals(UserRole.PROVIDER)){
						Provider provider = Provider.findByUser(user);						
						Company company = Company.findByOwner(provider.user.id);
						SourceRestriction sourceRestriction = SourceRestriction.findByProvider(provider);
						
						provider.sourceRestriction = null;
						provider.save();
						provider.refresh();
						sourceRestriction.provider = null;
						sourceRestriction.save();
						sourceRestriction.refresh();
						
						sourceRestriction.delete();
						provider.delete();
						company.delete();
					}
					
					activation.delete();
					user.delete();
					cleanUser++;
				} else if(activation.motive.equals(ActivationMotive.findByMotive(ActivationMotive.RECOVERY))){
					activation.delete();
					cleanPassRec++;
				}
			}
		}
		Logger.info("[ *** JOB: USER CLEAN *** ] - Users deleted  : " + cleanUser );
		Logger.info("[ *** JOB: USER CLEAN *** ] - PassRec deleted: " + cleanPassRec );
		Logger.info("[ *** JOB: USER CLEAN *** ] - END");
		
		run_job_requestTimer();
		run_job_proposalTimer();
		
	}
	
	/*
	 * This Job runs with 'cron.time.timer.request' interval (check application.conf file) and change 
	 * switch if the requests are open or not for proposals
	 */

	public void run_job_requestTimer() throws Exception {
		Logger.info("[ *** JOB: REQUEST TIMER *** ] - Start");
		int blocked_requests = 0;
		
		// Get all active requests
		List<TravelRequest> requests = TravelRequest.findByState(RequestState.findByName(RequestState.ACTIVE));
		
		for (TravelRequest travelRequest : requests) {
			
			// ONLY run for new requests
			if(travelRequest.validTo != null){
				if(!RequestHelper.isValidForProposals(travelRequest)){
					
					blocked_requests++;
					
					// IF has proposals 
					if(RequestHelper.hasPublicProposals(travelRequest)){
						// NOTIFY USER
						travelRequest.state = RequestState.findByName(RequestState.ACCEPTED);
						travelRequest.save();
						UbeosMailer.alertUserAcceptProposal(travelRequest.consumer.user, travelRequest);
	
					} else {
						// ELSE CLOSE
						travelRequest.state = RequestState.findByName(RequestState.CLOSED);
						// NOTIFY USER 
						UbeosMailer.alertUserNoProposal(travelRequest.consumer.user, travelRequest);
						
						// Get ignored proposals and close them
						Set<Proposal> proposals_ignored = travelRequest.proposals;
						for (Proposal proposal : proposals_ignored) {
							if(!proposal.ispublic){
								proposal.state = ProposalState.findByName(ProposalState.CLOSED);
								proposal.save();
							}
						}
	
					}
					travelRequest.save();
				} else {
					// Alert user that soon will need to accept one of the proposals
					long time_left = helpers.RequestHelper.getTimeLeftOpen(travelRequest);
				    long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(time_left);
				    if(hours <= 24 && hours >23){
				    	UbeosMailer.alertUserForAcceptancePeriod(travelRequest.consumer.user, travelRequest);
				    }
				}
			}
		}
		
		
		Logger.info("[ *** JOB: REQUEST TIMER *** ] - requests blocked  : " + blocked_requests );
		Logger.info("[ *** JOB: REQUEST CLOSER *** ] - End");
		
	}
	
	
	
	/*
	 * This Job runs with 'cron.time.timer.proposal' interval (check application.conf file) and change 
	 * switch if the proposal are open or not for acceptance
	 */

	public void run_job_proposalTimer() throws Exception {
		Logger.info("[ *** JOB: PROPOSAL TIMER *** ] - Start");
		int closed_proposals = 0;
		
		// Get all active requests (blocked)
		List<TravelRequest> requests = TravelRequest.findActiveAndBloqued();
		
		for (TravelRequest travelRequest : requests) {
			if( ProposalHelper.getTimeLeftOpen(travelRequest) <= 0){
				// CLOSE
				List<Proposal> proposals = Proposal.find("byRequestAndIspublic", travelRequest, true).fetch();
				for (Proposal prop : proposals) {
					if(prop.state == ProposalState.findByName(ProposalState.ACTIVE)){
						RejectMotive motive = RejectMotive.findByName(RejectMotive.EXPIRED);
						Proposals.rejectProposalAndNotify(prop, motive.id, null);
					}
				}
				
				if(travelRequest.state != RequestState.findByName(RequestState.CLOSED)){
					travelRequest.state = RequestState.findByName(RequestState.CLOSED);
					travelRequest.save();
					
					UbeosMailer.closeRequestRejectedAllProposals(travelRequest.consumer.user, travelRequest);
				}
				
			}

		}
		
		Logger.info("[ *** JOB: PROPOSAL TIMER *** ] - requests blocked  : " + closed_proposals );
		Logger.info("[ *** JOB: PROPOSAL CLOSER *** ] - End");
	}
}