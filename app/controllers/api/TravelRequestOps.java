package controllers.api;

import helpers.LoggerHelper;
import helpers.api.RequestApiBuilder;
import helpers.api.TokenHelper;
import helpers.api.TravelRequestApiHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import models.Accommodation;
import models.AppToken;
import models.Consumer;
import models.Country;
import models.Experience;
import models.Location;
import models.Meal;
import models.RequestState;
import models.Transport;
import models.TravelRequest;
import models.User;
import models.UserRole;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.With;
import controllers.MyController;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class TravelRequestOps extends MyController{
	
	@Unrestricted
	public static void create(JsonObject body) {
		LoggerHelper.api_info_Logger("create request");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

//		String lang = TokenHelper.getApiLang(request.current());
//		Lang.change(lang);
		

		RequestApiBuilder req_api = new Gson().fromJson(body, RequestApiBuilder.class);
		
		LoggerHelper.api_info_Logger("->" + req_api);
		
		TravelRequest travelRequest = TravelRequestApiHelper.generateFromApi(req_api);
		travelRequest.save();
		
				
		String returnJson = "{ \"code\": 200";
//		
//		for (Country country : countries) {
//			returnJson += "{ \"id\": " + country.id +", \"name\": \"" + Messages.get("country."+country.code) + "\"},";
//		}
//		returnJson = returnJson.substring(0,returnJson.length()-1);
		returnJson += "}";
		
		renderJSON(returnJson);
	}
	
	@Unrestricted
	public static void index() {
		LoggerHelper.api_info_Logger("get request index");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		// Get Logged user
		User user = User.findById(appToken.user);
		isValidUser(user);
		
		// Get Travel Requests accordingly with user profile
		List<TravelRequest> requests = new ArrayList<TravelRequest>();
		if(user.userRole.name.equals(UserRole.CONSUMER)){
			Consumer consumer = Consumer.findByUser(user);
			RequestState state = RequestState.findByName(RequestState.ACTIVE);
			requests = TravelRequest.find("byConsumerAndEnableAndState", consumer, true, state).fetch();			
		} else {
			forbidden();
		}
		
		
		LoggerHelper.api_info_Logger("-> "+requests);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
		String returnJson = gson.toJson(requests);
		LoggerHelper.api_info_Logger("-> "+returnJson);		
		renderJSON(returnJson);
	}
	

	
}
