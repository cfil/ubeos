package helpers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Consumer;
import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.RequestState;
import models.TravelRequest;
import models.User;
import play.mvc.Scope.Session;
import play.mvc.Util;

public class RequestHelper {

	private static final Long DAYS_3  = 259200000L;
	private static final Long WEEK_1  = 604800000L;
	private static final Long WEEK_2  = 1209600000L;
	private static final Long WEEK_3  = 1814400000L;
	private static final Long MONTH_1 = 2592000000L;
	private static final Long MONTH_2 = 5184000000L;
	
	@Util
	public static boolean hasNewProposals(TravelRequest request){
		
		for (Proposal proposal : request.proposals) {
			if(proposal.isNew == Proposal.ISNEW){
				return true;
			}
		}
		
		return false;
	}
	
	@Util
	public static boolean hasEditedProposals(TravelRequest request){
		
		for (Proposal proposal : request.proposals) {
			if(proposal.isNew == Proposal.EDITED){
				return true;
			}
		}
		
		return false;
	}
	
	@Util
	public static int getNewOrEditedProposals(User user){
		int total = 0;
		
		Consumer consumer = Consumer.findByUser(user);
		if (consumer == null){
			return total;
		}
		
		List<TravelRequest> travelRequests = TravelRequest.findByStateAndConsumer(RequestState.findByName(RequestState.ACTIVE), consumer);
		for (TravelRequest travelRequest : travelRequests) {
			for (Proposal proposal : travelRequest.proposals) {
				if(proposal.isNew == Proposal.ISNEW || proposal.isNew == Proposal.EDITED){
					total++;
				}
			}
		}
		
		return total;
	}
	
	@Util
	public static int getPendingFeedback(User user){
		int total = 0;
		
		Consumer consumer = Consumer.findByUser(user);
		if (consumer == null){
			return total;
		}
		
		List<TravelRequest> travelRequests = TravelRequest.findByStateAndConsumer(RequestState.findByName(RequestState.FEEDBACK), consumer);
		total = travelRequests.size();
		return total;
		
	}
	
	@Util
	public static boolean hasProposalProvider(TravelRequest request){
		
		Session session = Session.current();
		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		
		if(provider == null){
			return false;
		}
		
		
		for (Proposal proposal : request.proposals) {
			if(proposal.provider == provider){
				return true;
			}
		}
		
		return false;
	}
	
	@Util
	public static boolean hasActiveProposalProvider(TravelRequest request){
		
		Session session = Session.current();
		User user = UserTypeHelper.getLoggedUser(session);
		Provider provider = Provider.findByUser(user);
		
		if(provider == null){
			return false;
		}
				
		for (Proposal proposal : request.proposals) {
			if(proposal.provider == provider){
				if(proposal.state.equals(ProposalState.findByName(ProposalState.ACTIVE))){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Util
	public static boolean hasPublicProposals(TravelRequest travelRequest){
		for (Proposal proposal : travelRequest.proposals) {
				if(proposal.state.equals(ProposalState.findByName(ProposalState.ACTIVE)) && proposal.ispublic ){
					return true;
				}
		}
		return false;
	}
	
	@Util
	public static boolean isValidForProposals(TravelRequest request){
		
		Date today = new Date();
		boolean isValid = true;
		
		switch (request.validTo.intValue()) {
		case 0:
			if( (today.getTime() - DAYS_3) > request.created_at.getTime()){
				isValid = false;
			}
			break;
		case 1:
			if( (today.getTime() - WEEK_1) > request.created_at.getTime()){
				isValid = false;
			}
			break;

		case 2:
			if( (today.getTime() - WEEK_2) > request.created_at.getTime()){
				isValid = false;
			}
			break;
		case 3:
			if( (today.getTime() - WEEK_3) > request.created_at.getTime()){
				isValid = false;
			}
			break;
		case 4:
			if( (today.getTime() - MONTH_1) > request.created_at.getTime()){
				isValid = false;
			}
			break;
		case 5:
			if( (today.getTime() - MONTH_2) > request.created_at.getTime()){
				isValid = false;
			}
			break;
		}
		
		return isValid;
	}
	
	@Util
	public static Long getTimeLeftOpen(TravelRequest request){
		
		if(!isValidForProposals(request)){
			return 0L;
		}
		
//		Date today = new Date();
//		Long diff = today.getTime() - request.created_at.getTime();
//		
//		if( request.validTo == 0){
//			if(diff < DAYS_3){
//				return DAYS_3 - diff;
//			} else return 0L;
//		}
//		if( request.validTo == 1){
//			if(diff < WEEK_1){
//				return WEEK_1 - diff;
//			} else return 0L;
//		}
//		if( request.validTo == 2){
//			if(diff < WEEK_2){
//				return WEEK_2 - diff;
//			} else return 0L;
//		}
//		if( request.validTo == 3){
//			if(diff < WEEK_3){
//				return WEEK_3 - diff ;
//			} else return 0L;
//		}
//		if( request.validTo == 4){
//			if(diff < MONTH_1){
//				return MONTH_1 - diff ;
//			} else return 0L;
//		}
//		if( request.validTo == 5){
//			if(diff < MONTH_2){
//				return MONTH_2 - diff;
//			} else return 0L;
//		}
//		return 0L;
		
		Date today = new Date();
		long date_final;
		
		if( request.validTo == 0){
			date_final = request.created_at.getTime() + DAYS_3;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;
		}
		if( request.validTo == 1){
			date_final = request.created_at.getTime() + WEEK_1;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;
		}
		if( request.validTo == 2){
			date_final = request.created_at.getTime() + WEEK_2;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;
		}
		if( request.validTo == 3){
			date_final = request.created_at.getTime() + WEEK_3;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;		}
		if( request.validTo == 4){
			date_final = request.created_at.getTime() + MONTH_1;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;
		}
		if( request.validTo == 5){
			date_final = request.created_at.getTime() + MONTH_2;
			long diff = date_final - today.getTime();
			if(diff > 0){
				return diff;
			} else return 0L;
		}
		return 0L;
		
	}
	
	@Util
	public static Proposal getAcceptedProposal(TravelRequest request){
		
		ProposalState prop_state = ProposalState.findByName(ProposalState.FEEDBACK);
		
		Set<Proposal> props = request.proposals; 
		for (Proposal proposal : props) {
			if (proposal.state == prop_state){
				return proposal;
			}
		}
		return null;
	}
	
	@Util
	public static int getNextRequestImage(Consumer consumer){
		TravelRequest lastRequest = TravelRequest.find("consumer_id = ? order by created_at desc",consumer).first();
		if(lastRequest != null){
			if(lastRequest.image == 8){
				return 1;
			} else {
				return lastRequest.image + 1;
			}
		} else {
			return 1;
		}

	}
	
	@Util
	public static int getTotalRealProposals(TravelRequest travelRequest){
		int proposals = 0;
		for (Proposal proposal : travelRequest.proposals) {
				if(proposal.ispublic){
					proposals++;
				}
		}
		return proposals;
	}

	@Util
	public static Long getValidToTimeInMillis(TravelRequest request) {
		switch (request.validTo.intValue()) {
		case 0:
			return request.created_at.getTime() + DAYS_3;
		case 1:
			return request.created_at.getTime() + WEEK_1;
		case 2:
			return request.created_at.getTime() + WEEK_2;
		case 3:
			return request.created_at.getTime() + WEEK_3;
		case 4:
			return request.created_at.getTime() + MONTH_1;
		case 5:
			return request.created_at.getTime() + MONTH_2;
		}
		return null;
		
	}
	
	

}