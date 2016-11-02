package jobs;

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


@On("cron.time.register.no.request")
public class RegisterNoRequest extends Job {

	/*
	 * Time passed to notify of no request submited
	 * value in millis (604800000 = 7 days)
	 */
	private static long TIME_OF_6_DAYS = 518400000;
	private static long TIME_TO_NOTTIFY = 604800000;
	
	
	/*
	 * This Job runs with 'cron.time.register.no.request' interval (check application.conf file) and send an email
	 * to the user that registed at UBEOS but didn't submitted a request yet
	 */
	@Override
	public void doJob() throws Exception {
		Logger.info("[ *** JOB: REGISTER NO REQUEST *** ] - Start");
	
		int totalMailSent = 0;
		
		//Get all consumers
		List<Consumer> consumers = Consumer.findAll();
		for (Consumer consumer : consumers) {
			// Get all requests for each consumer
			List<TravelRequest> requests = TravelRequest.find("byConsumer", consumer).fetch();
			
			// Only work if it has requests
			if(requests.isEmpty()){
				// Get how long was the request submited
				long created = consumer.created_at.getTime();
				long now = new Date().getTime();
				long diff = now - created;

				//If the request was submitted for more than 6 days and is lower than time to notify...
				if(diff > TIME_OF_6_DAYS && diff < TIME_TO_NOTTIFY){
					// Send mail...
					UbeosMailer.registerNoRequest(consumer.user);
					totalMailSent++;				
				}
			}
			
		}
		
		Logger.info("[ *** JOB: REGISTER NO REQUEST *** ] - Users Notified: " + totalMailSent );
		Logger.info("[ *** JOB: REGISTER NO REQUEST *** ] - END");
	}
}