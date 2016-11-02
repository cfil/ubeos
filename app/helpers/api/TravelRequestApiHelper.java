package helpers.api;

import java.util.Date;

import models.Accommodation;
import models.Country;
import models.Experience;
import models.Location;
import models.Meal;
import models.Transport;
import models.TravelRequest;

public class TravelRequestApiHelper {
	
	public static TravelRequest generateFromApi(RequestApiBuilder req_api){
		
		TravelRequest travelRequest = new TravelRequest();
		
		travelRequest.title = req_api.getName();	
		travelRequest.country_location = Country.findById(req_api.getCountryId());
		travelRequest.location = Location.findById(req_api.getLocationId());
		travelRequest.maxBudget = req_api.getBudgetTypeId();
		travelRequest.validTo = req_api.getExpirationTypeId();
		
		travelRequest.wantTransportation = req_api.isNeedsTransportation();
		travelRequest.wantAccommodation = req_api.isNeedsHousing();
		travelRequest.wantActivities = req_api.isNeedsActivities();
		
		travelRequest.city_to = req_api.getDestinationCity();
		travelRequest.country_to = Country.findById(req_api.getDestinationCountryId());

		travelRequest.dateFrom = new Date(req_api.getStartDate());
		travelRequest.dateTo = new Date(req_api.getEndDate());
		travelRequest.nights = req_api.getAmountOfNights();
		travelRequest.flexible = req_api.isFlexible();
		travelRequest.adults = (long) req_api.getAmountOfAdults();
		travelRequest.children = (long) req_api.getAmountOfChildren();

		travelRequest.transport = Transport.findById(req_api.getTransportationTypeId());
		travelRequest.descriptionThree = req_api.getTransportationDetails();
		
		travelRequest.accommodation = Accommodation.findById(req_api.getHousingTypeId());
		
		travelRequest.roomType = req_api.getRoomTypology();
		travelRequest.meal = Meal.findById(req_api.getMealTypeId());
		travelRequest.stars = req_api.getMinimumStartAmount();
		
		travelRequest.descriptionPeople = req_api.getChildrenDescription();
		travelRequest.descriptionFour = req_api.getExperiencesDescription();
//		travelRequest.experiences = Experience.findById(req_api)
		
		return travelRequest;
		
	}
	
}
