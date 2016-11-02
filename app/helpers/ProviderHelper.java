package helpers;

import java.util.List;

import models.Company;
import models.Provider;
import models.ProviderRating;
import models.User;
import play.mvc.Util;

public class ProviderHelper {

	@Util
	public static double calcCompanyRating(Provider provider){
		
		Company company = provider.company;
		
		long nrRating = 0;
		long totRating = 0;
		
		List<ProviderRating> ratings = ProviderRating.findByCompany(company);
		
		for (ProviderRating providerRating : ratings) {
			totRating += providerRating.value;
			nrRating++;
		}
		
		if(nrRating > 0){
			return totRating/nrRating;
		} else{
			return 0.0;
		}
	}
	
	@Util
	public static boolean isCompanyOwner(User user){
		Company company = Company.findByOwner(user.id);
		if(company==null){
			return false;
		} else {
			return true;
		}
		
	}
	
}
