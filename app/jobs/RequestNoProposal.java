package jobs;

import helpers.LoggerHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import notifiers.UbeosMailer;

import models.Activation;
import models.ActivationMotive;
import models.Company;
import models.Consumer;
import models.Provider;
import models.RequestState;
import models.SourceRestriction;
import models.TravelRequest;
import models.User;
import models.UserRole;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;


@On("cron.time.request.no.proposal")
public class RequestNoProposal extends Job {

	/*
	 * Time passed to notify of no proposals
	 * value in millis (259 200 000 = 3 days)
	 */
	private static long TIME_OF_2_DAYS = 172800000;
	private static long TIME_TO_NOTTIFY = 259200000;
//	private static long DAYS_TO_NOTTIFY = 3;
	
	/*
	 * This Job runs with 'cron.time.request.no.proposal' interval (check application.conf file) and send an email
	 * to the user that submited a request and haven't received any proposals yet
	 */
	@Override
	public void doJob() throws Exception {
		Logger.info("[ *** JOB: REQUEST NO PROPOSAL *** ] - Start");
	
		int totalMailSent = 0;
		RequestState state = RequestState.findByName(RequestState.ACTIVE);
		
		// Get all consumeres
		List<Consumer> consumers = Consumer.findAll();
		for (Consumer consumer : consumers) {
			// Get all ACTIVE requests for each consumer
			List<TravelRequest> requests = TravelRequest.findByStateAndConsumer(state, consumer);
			
			// By default.. do not notify
			boolean notify = false;
			if(requests != null){
				for (TravelRequest travelRequest : requests) {
					// for each request without proposals
					if (travelRequest.proposals.isEmpty()){
						// check time passed since submission
						long created = travelRequest.created_at.getTime();
						long now = new Date().getTime();
						long diff = now - created;

						// if its between two and 3 days... notify
						if(diff > TIME_OF_2_DAYS && diff < TIME_TO_NOTTIFY){
							notify = true;
						}
					}
				}
			}
			if(notify){
				UbeosMailer.requestNoProposal(consumer.user);
				totalMailSent++;
			}
		}
		
		Logger.info("[ *** JOB: REQUEST NO PROPOSAL *** ] - Users Notified: " + totalMailSent );
		Logger.info("[ *** JOB: REQUEST NO PROPOSAL *** ] - END");
	}
}