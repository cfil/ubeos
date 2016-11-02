package controllers.api;

import helpers.LoggerHelper;
import helpers.api.RequestApiBuilder;
import helpers.api.TokenHelper;
import helpers.api.TravelRequestApiHelper;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import models.Accommodation;
import models.AppToken;
import models.Country;
import models.Experience;
import models.Location;
import models.Meal;
import models.Transport;
import models.TravelRequest;
import models.Versioning;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.With;
import controllers.MyController;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Versionings extends MyController{
	
	@Unrestricted
	public static void getVersion(String table_name) {
		LoggerHelper.api_info_Logger("Get table versions");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}

		Versioning tableVersion = Versioning.findByTableName(table_name);
		if(tableVersion == null){
			forbidden();
		}
				
		String returnJson = "{ \"table\": \""+ tableVersion.ubeos_table +"\", \"version\": \""+ tableVersion.current_version +"\"}";
		
		renderJSON(returnJson);
	}
	

	
}
