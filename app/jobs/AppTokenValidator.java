package jobs;

import java.util.Date;
import java.util.List;
import java.util.Set;

import models.AppToken;
import models.Proposal;
import models.TravelRequest;
import models.User;
import notifiers.UbeosMailer;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;


@On("cron.time.token.validator")
public class AppTokenValidator extends Job {

	private static long HOURS_24 = 86400000;
	
	@Override
	public void doJob() throws Exception {
		Logger.info("[ *** JOB: TOKEN VALIDATOR *** ] - Start");

		Long disabled_tokens = 0L;
		
		Date today = new Date();
		long now_time = today.getTime();
		long valid_time = now_time-HOURS_24;

		List<AppToken> tokens = AppToken.findByEnable(true);
		for (AppToken appToken : tokens) {
			if(appToken.updated_at.getTime() < valid_time){
				appToken.enable = false;
				appToken.save();
				disabled_tokens++;
			}
		}
					
		Logger.info("[ *** JOB: TOKEN VALIDATOR *** ] - "+ disabled_tokens+" disabled tokens");
		Logger.info("[ *** JOB: TOKEN VALIDATOR *** ] - END");		
	}
	
}