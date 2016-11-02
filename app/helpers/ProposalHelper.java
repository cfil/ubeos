package helpers;

import java.util.Date;
import java.util.List;

import controllers.TravelRequests;
import controllers.deadbolt.RestrictedResource;

import models.Consumer;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.RequestState;
import models.TravelRequest;
import models.User;
import models.UserRole;
import play.mvc.Scope.Session;
import play.mvc.Util;

public class ProposalHelper {

	private static final Long DAYS_3 = 259200000L;
	
	@Util
	public static Long getTimeLeftOpen(TravelRequest request){
		
		Date today = new Date();
		Long valid_to_time = RequestHelper.getValidToTimeInMillis(request);
		if(valid_to_time == null){
			return 0L;
		}
		Long limit_time = valid_to_time + DAYS_3;

		if( today.getTime() < limit_time ){
			return limit_time - today.getTime();
		}
		
		return 0L;
		
	}
	
	@Util
	public static boolean isIgnore(TravelRequest request, User user) {
		
		Provider provider = Provider.findByUser(user);
		Proposal proposal = Proposal.findByRequestAndProviderIgnored(request, provider);
		
		if(proposal != null){
			if(!proposal.state.proposalState.equals(ProposalState.DISABLED)){
				return true;
			}
		} else {
			return false;
		}
		return false;
	}
	
	@Util
	public static int getPendingFeedback(User user){
		int total = 0;
		
		Provider provider = Provider.findByUser(user);
		if (provider == null){
			return total;
		}
		
		List<Proposal> proposals = Proposal.findByStateAndProvider(ProposalState.findByName(ProposalState.FEEDBACK), provider);
		
		total = proposals.size();
		return total;
		
	}
}
