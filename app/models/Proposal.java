package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.Http.Request;

@Entity
@Table(name = "proposal")
public class Proposal extends MyModel {
	
	public static final int READED = 0;
	public static final int ISNEW = 1;
	public static final int EDITED = 2;
	
	public static final int MAX_DESCRIPTION_SIZE = 65535;
	
	@ManyToOne
	public ProposalState state;

	@Column(name = "description",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String description;

	@Column(name = "description_two",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionTwo;
	
	@Column(name = "description_three",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionThree;
	
	@Column(name = "description_four",length = 65535,columnDefinition="Text")
	@MaxSize(value = MAX_DESCRIPTION_SIZE)
	public String descriptionFour;
	
	@Required
	@Column(name = "price")
	@Min(1)
	public double price;

	@Column(name = "ispublic")
	public boolean ispublic = true;
	
	@ManyToOne
	public Provider provider;
	
	@ManyToOne
	public TravelRequest request;
	
	@MaxSize(value = 150)
	@Column(name = "city_from")
	public String city_from;
	
	@MaxSize(value = 150)
	@Column(name = "city_to")
	public String city_to;
	
	@Transient
	public String date_from;
	
	@Column(name = "datefrom")
	@Temporal(TemporalType.DATE)
	public Date dateFrom;

	@Transient
	public String date_to;
	
	@Column(name = "dateto")
	@Temporal(TemporalType.DATE)
	public Date dateTo;
	
	@ManyToOne
	public Accommodation accommodation;
	
	@ManyToOne
	public Transport transport;
	
	@ManyToOne
	public Meal meal;
		
	@ManyToMany(fetch = FetchType.LAZY)
	public Set<Experience> experiences;
	
	@OneToOne
	public Billing billing;
	
	@Required
	@Min(0)
	@Column(name="nights")
	public int nights;
	
	// Is new: 0 if readed, 1 if new, 2 if edited and not readed
	@Column(name="is_new")
	public int isNew = ISNEW;
	
	@MaxSize(value = 150)
	@Column(name = "title")
	public String title;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Proposal(){
	}

	public Proposal(String description, double price){
		this.price = price;
		this.description = description;
	}
	
	public static List<Proposal> getPendingProposals(){
		return Proposal.find("byState", ProposalState.findByName(ProposalState.ACCEPTED)).fetch();
	}

	public static List<Proposal> findByRequest(TravelRequest request){
		return Proposal.find("byRequestAndIsPublic",request, true).fetch();
	}
	
	public static List<Proposal> findByRequestAndProvider(TravelRequest request, Provider provider){
		return Proposal.find("byRequestAndProviderAndIsPublic",request, provider, true).fetch();
	}

	public static List<Proposal> findByRequestAndState(TravelRequest travelRequest, ProposalState state) {
		return Proposal.find("byRequestAndState",travelRequest, state).fetch();
	}
	
	public static List<Proposal> findByState(ProposalState state) {
		return Proposal.find("byState", state).fetch();
	}
	
	public static List<Proposal> findByRequestAndCompany(TravelRequest request, Company company){
		List<Proposal> ret_list = new ArrayList<Proposal>();
		
		List<Provider> providers = Provider.findByCompany(company);
		for (Provider provider2 : providers) {
			ret_list.addAll(findByRequestAndProvider(request, provider2));
		}
		return ret_list;
	}

	public static List<TravelRequest> removeIgnored(List<TravelRequest> requests, Provider provider) {
		
		List<TravelRequest> travelRequests = new ArrayList<TravelRequest>();
		
		for (TravelRequest travelRequest : requests) {
			Proposal proposal = Proposal.find("byRequestAndProviderAndIsPublic", travelRequest, provider, false).first();
			if(proposal == null){
				travelRequests.add(travelRequest);
			}
			
		}
		
		return travelRequests;
	}
	
	public static int countProposals(TravelRequest travelRequest) {
		int total = 0;

		for (Proposal proposal : travelRequest.proposals) {
			if(proposal.ispublic){
				total++;
			}
		}
		
		return total;
	}
	
	public static boolean findForPendingTab(User user){
		
		Provider provider = Provider.findByUser(user);
		
//		List<Proposal> proposals = Proposal.find("provider_id = ? order by created_at desc",provider).fetch();
		List<Proposal> proposals = Proposal.findByProviderCompany(provider);
		for (Proposal proposal : proposals) {
			if(proposal.state.proposalState.equals(ProposalState.ACCEPTED) ||  proposal.state.proposalState.equals(ProposalState.FEEDBACK)){
				if(!proposal.state.proposalState.equals(ProposalState.DISABLED)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean findForClosedTab(User user){
		
		Provider provider = Provider.findByUser(user);
		
//		List<Proposal> proposals = Proposal.find("provider_id = ? order by created_at desc",provider).fetch();
		List<Proposal> proposals = Proposal.findByProviderCompany(provider);
		for (Proposal proposal : proposals) {
			if(proposal.state.proposalState.equals(ProposalState.CLOSED) || proposal.state.proposalState.equals(ProposalState.REJECTED)){
				return true;
			}
		}
		
		return false;
	}

	public static boolean findForIgnoredTab(User user){
		
		Provider provider = Provider.findByUser(user);
		
//		List<Proposal> proposals = Proposal.find("provider_id = ? order by created_at desc",provider).fetch();
		List<Proposal> proposals = Proposal.findByProviderCompany(provider);
		for (Proposal proposal : proposals) {
			if(!proposal.ispublic){
				if(proposal.request.state == RequestState.findByName(RequestState.ACTIVE) && !proposal.state.proposalState.equals(ProposalState.DISABLED)){
					return true;
				}
			}
		}
		
		return false;
	}

	public static Proposal findByRequestAndProviderIgnored(
			TravelRequest request, Provider provider) {
		return Proposal.find("byRequestAndProviderAndIsPublic",request, provider, false).first();
	
	}

	public static List<Proposal> findByStateAndProvider(ProposalState state, Provider provider) {
		return Proposal.find("byStateAndProvider",state, provider).fetch();
	}
	
	public static List<Proposal> findForHistory(Provider provider) {
//		List<Proposal> proposals = Proposal.find("byProvider", provider).fetch();
		List<Proposal> proposals = Proposal.findByProviderCompany(provider);
		List<Proposal> prop_ret = new ArrayList<Proposal>();
		for (Proposal proposal : proposals) {
			if( proposal.state == ProposalState.findByName(ProposalState.CLOSED) || proposal.state == ProposalState.findByName(ProposalState.REJECTED)){
				prop_ret.add(proposal);
			}
		}
		return prop_ret;
	}
	
	public static List<Proposal> findByProviderCompany(Provider provider) {
		List<Proposal> prop_ret = new ArrayList<Proposal>();

		Company company = provider.company;
		List<Provider> providers = Provider.findByCompany(company);

		for (Provider prov : providers) {
			List<Proposal> proposals = Proposal.find("byProvider", prov).fetch();
			for (Proposal proposal : proposals) {
				prop_ret.add(proposal);
			}
		}
		
		return prop_ret;
	}
	
	public static List<Proposal> findByProviderCompanyAndState(Provider provider, ProposalState state) {
		List<Proposal> prop_ret = new ArrayList<Proposal>();

		Company company = provider.company;
		List<Provider> providers = Provider.findByCompany(company);

		for (Provider prov : providers) {
			List<Proposal> proposals = Proposal.find("byProviderAndState", prov, state).fetch();
			for (Proposal proposal : proposals) {
				prop_ret.add(proposal);
			}
		}
		
		return prop_ret;
	}
	
}
