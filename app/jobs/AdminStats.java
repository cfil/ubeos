package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Company;
import models.Proposal;
import models.Provider;
import models.ReplyStat;
import models.RequestState;
import models.TravelRequest;
import models.User;
import notifiers.UbeosMailer;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;


@On("cron.time.admin")
public class AdminStats extends Job {

	private static final String[] admins = {"carloscarvalho@ubeos.com","hugoduarte@ubeos.com", "brunomota@boldint.com"};
	
	private static long HOURS_24 = 86400000;
	
	@Override
	public void doJob() throws Exception {
		if(Play.id.equals("prod")){
			Logger.info("[ *** JOB: ADMIN STATS *** ] - Start");
	
			Date today = new Date();
			long now_time = today.getTime();
			long yesterday_time = now_time-HOURS_24;
					
			List<User> all_users = User.findAll(); 
			List<TravelRequest> all_requests = TravelRequest.findAll();
			List<Proposal> all_proposals = Proposal.findAll();
	
			long total_users = all_users.size();
			long total_requests = all_requests.size();
			long total_proposals = all_proposals.size();
	
			long new_users = getNewUsers(all_users, now_time, yesterday_time);
			long new_requests = getNewRequests(all_requests, now_time, yesterday_time);
			long new_proposals = getNewProposals(all_proposals, now_time, yesterday_time);
			
			
			/* Reply Stats */
			HashMap stats = new HashMap();
			List<Company> companies = Company.findAll();
			for (Company company : companies) {
				List<Provider> providers = Provider.findByCompany(company);
				for (Provider provider : providers) {
					if(provider.user.enable){
						List<ReplyStat> repliesStats = ReplyStat.findByProvider(provider);
						Long nr_received = 0L;
						Long nr_replied = 0L;
						for (ReplyStat replyStat : repliesStats) {
							nr_received++;
							if(replyStat.replied){
								nr_replied++;
							}
						}
						if(nr_received == 0){
							nr_received = 1L;
						}
						Logger.info("[ *** JOB: ADMIN STATS *** ] - "+company.name+" -> "+(nr_replied*100)/nr_received);
						stats.put(company.name, (nr_replied*100)/nr_received );
					}
				}
			}
			
			for (String admin : admins) {
				Logger.info("[ *** JOB: ADMIN STATS *** ] - Sending mail to " + admin);
				UbeosMailer.sendMailWithStats(admin, stats, total_users, total_requests, total_proposals, new_users, new_requests, new_proposals);
			}
			
			
			Logger.info("[ *** JOB: ADMIN STATS *** ] - END");	
		}
	}
	
	private long getNewUsers(List<User> all_users, long now_time, long yesterday_time){
		int ret = 0;
		for (User user : all_users) {
			if( (user.created_at.getTime() < now_time) && (user.created_at.getTime() > yesterday_time) ){
				ret++;
			}
		}
		return ret;
	}
	
	private long getNewRequests(List<TravelRequest> all_requests, long now_time, long yesterday_time){
		int ret = 0;
		for (TravelRequest travelRequest : all_requests) {
			if( (travelRequest.created_at.getTime() < now_time) && (travelRequest.created_at.getTime() > yesterday_time) ){
				ret++;
			}
		}
		return ret;
	}
	
	private long getNewProposals(List<Proposal> all_proposals, long now_time, long yesterday_time){
		int ret = 0;
		for (Proposal proposal : all_proposals) {
			if( (proposal.created_at.getTime() < now_time) && (proposal.created_at.getTime() > yesterday_time) ){
				ret++;
			}
		}
		return ret;
	}
}