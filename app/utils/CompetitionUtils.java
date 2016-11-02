package utils;

import java.util.List;
import java.util.Set;

import notifiers.UbeosMailer;

import models.Proposal;
import models.ProposalState;
import models.Provider;
import models.ProviderRating;
import models.TravelRequest;

public class CompetitionUtils {
	
	private final static double MINIMUM_VALUE_OFFER = 100.00;
		
	/**
	 * This method is the main method of our proposal competition System, it checks if other
	 * proposals were beaten and notify those providers 
	 * @param proposal
	 */
	public static void competitionSystem(Proposal proposal){
		return;
//		TravelRequest travelRequest = proposal.request;
		
//		Set<Proposal> proposals = travelRequest.proposals;
//		for (Proposal prop : proposals) {
//			if ((prop.id != proposal.id) && (prop.provider.id != proposal.provider.id) && (prop.state.proposalState.equals(ProposalState.ACTIVE)) ){
//			
//				boolean price = isPriceBetter(prop.price, proposal.price);
//				boolean rating = isRatingBetter(prop.provider, proposal.provider);
//				
//				if(price || rating){
//					UbeosMailer.proposalBeated(prop.provider.user, prop, price, rating);
//				}
//				
//			}
//		}
	
	}
	
	private static boolean isRatingBetter(Provider provider_old, Provider provider_new){
		
			
		if(ProviderRating.getProviderRating(provider_old) < ProviderRating.getProviderRating(provider_new)){
			return true;
		} else {
			return false;
		}
		
	}
	
	private static boolean isPriceBetter(double old_price, double new_price){
		if (new_price > MINIMUM_VALUE_OFFER){
			if(new_price < old_price){
				return true;
			} else {
				return false;
			}
		}
		return false;
			
	}
}
