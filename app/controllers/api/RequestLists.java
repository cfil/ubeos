package controllers.api;

import helpers.LoggerHelper;
import helpers.api.TokenHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.Accommodation;
import models.AppToken;
import models.Country;
import models.Experience;
import models.Location;
import models.Meal;
import models.Transport;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Util;
import play.mvc.With;
import controllers.MyController;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class RequestLists extends MyController{

	// Develop Branch
	
	@Unrestricted
	public static void getCountries() {
		LoggerHelper.api_info_Logger("Get Countries");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Country> countries = Country.all().fetch();
		
		
		Map<Long, String> countries_ret = new HashMap<Long, String>();
		for (Country country : countries) {
			countries_ret.put(country.id, Messages.get("country."+country.code));
		}
		Map<Long, String> sortedMap = sortByComparator(countries_ret);
		
		String returnJson = "[";
		for (Map.Entry<Long,String> entry : sortedMap.entrySet()) {
			returnJson += "{ \"id\": " + entry.getKey() +", \"name\": \"" + entry.getValue() + "\"},";			
		}
		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "]";
						
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getLocations(Long country_id) {
		LoggerHelper.api_info_Logger("Get Locations");

		if(country_id == null){
			notFound();
		}
		Country country = Country.findById(country_id);
		if(country == null){
			notFound();
		}
		
		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Location> locations = Location.findByCountry(country);
		if (locations.size() > 0) {		
			String returnJson = "[";
			
			for (Location location : locations) {
				returnJson += "{ \"id\": " + location.id +", \"name\": \"" + Messages.get(location.name) + "\"},";
			}
			returnJson = returnJson.substring(0,returnJson.length()-1);
			returnJson += "]";
			renderJSON(returnJson);
		} else {
			String returnJson ="[]";
			renderJSON(returnJson);
		}
	}
	
	@Unrestricted
	public static void getPreference() {
		LoggerHelper.api_info_Logger("Get Preference");
		
		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
						
		String returnJson = "[";
		returnJson += "{ \"id\": 50, \"name\": \"" + Messages.get("request.best.price") + "\"},";
		returnJson += "{ \"id\": 1000, \"name\": \"" + Messages.get("request.balanced") + "\"},";
		returnJson += "{ \"id\": 5000, \"name\": \"" + Messages.get("request.quality") + "\"}";
		
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getRequestValidity() {
		LoggerHelper.api_info_Logger("Get RequestValidity");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		String returnJson = "[";
		returnJson += "{ \"id\": 0, \"name\": \"" + Messages.get("request.valid.to.3days") + "\"},";
		returnJson += "{ \"id\": 1, \"name\": \"" + Messages.get("request.valid.to.1week") + "\"},";
		returnJson += "{ \"id\": 2, \"name\": \"" + Messages.get("request.valid.to.2week") + "\"},";
		returnJson += "{ \"id\": 3, \"name\": \"" + Messages.get("request.valid.to.3week") + "\"},";
		returnJson += "{ \"id\": 4, \"name\": \"" + Messages.get("request.valid.to.1month") + "\"}";
		
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getTransports() {
		LoggerHelper.api_info_Logger("Get Transports");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Transport> transports = Transport.all().fetch();
				
		String returnJson = "[";
		
		for (Transport transport : transports) {
			returnJson += "{ \"id\": " + transport.id +", \"name\": \"" + Messages.get(transport.name) + "\"},";
		}
		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getAccommodation() {
		LoggerHelper.api_info_Logger("Get Accommodations");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Accommodation> accommodations = Accommodation.all().fetch();
				
		String returnJson = "[";
		
		for (Accommodation accommodation : accommodations) {
			returnJson += "{ \"id\": " + accommodation.id +", \"name\": \"" + Messages.get(accommodation.name) + "\"},";
		}
		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getMeals() {
		LoggerHelper.api_info_Logger("Get Meals");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Meal> meals = Meal.all().fetch();
				
		String returnJson = "[";
		
		for (Meal meal : meals) {
			returnJson += "{ \"id\": " + meal.id +", \"name\": \"" + Messages.get(meal.name) + "\"},";
		}
		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void getExperiences() {
		LoggerHelper.api_info_Logger("Get Experiences");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		String lang = TokenHelper.getApiLang(request.current());
		Lang.change(lang);
		
		List<Experience> experiences = Experience.all().fetch();
				
		String returnJson = "[";
		
		for (Experience experience : experiences) {
			returnJson += "{ \"id\": " + experience.id +", \"name\": \"" + Messages.get(experience.name) + "\"},";
		}
		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "]";
		
		renderJSON(returnJson);
	}
	
	@Util
	private static Map<Long, String> sortByComparator(Map<Long, String> unsortMap) {

		// Convert Map to List
		List<Map.Entry<Long,String>> list = 
			new LinkedList<Map.Entry<Long,String>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Long,String>>() {
			public int compare(Map.Entry<Long,String> o1,
                                           Map.Entry<Long,String> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Long,String> sortedMap = new LinkedHashMap<Long,String>();
		for (Iterator<Map.Entry<Long,String>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Long,String> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
}
