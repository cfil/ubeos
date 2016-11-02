package controllers.api;

import helpers.LoggerHelper;
import helpers.api.TokenHelper;
import models.AppToken;
import models.User;
import play.mvc.Http.Request;
import play.mvc.With;
import utils.StringUtils;
import controllers.MyController;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Unrestricted;

@With(Deadbolt.class)
public class Authentication extends MyController{

	@Unrestricted
	public static void login() {
		LoggerHelper.api_info_Logger("API LOGIN");
		
		if( !Request.current().headers.containsKey("usr") || !Request.current().headers.containsKey("pwd") ){
			renderJSON("{\"code\": 200, message: \"Fields Required \"}");
		}
		
		String email = Request.current().headers.get("usr").value();
		String password = Request.current().headers.get("pwd").value();
		
		User user = User.findByEmail(email);
		if(user == null ){
			forbidden();
		}
		
		boolean isValid = user.checkPassword(password);
		if(!isValid){
			forbidden();
		} else {
			AppToken appToken = AppToken.findByUser(user);
			if(appToken != null){
				if (appToken.enable){
					renderJSON("{\"code\": 200, \"token\": \"" + appToken.a_token + "\", \"refresh_token\": \""+ appToken.r_token +"\", \"updated\": \""+ appToken.updated_at +"\"}");
				} else {
					appToken.a_token = helpers.api.TokenHelper.generate_A_Token(user);
					appToken.r_token = helpers.api.TokenHelper.generate_R_Token(user);
					appToken.enable = true;
					appToken.save();
					appToken.refresh();
					renderJSON("{\"code\": 200, \"token\": \"" + appToken.a_token + "\", \"refresh_token\": \""+ appToken.r_token +"\", \"updated\": \""+ appToken.updated_at +"\"}");
				}
			} else {
				AppToken newToken = new AppToken(user.id, TokenHelper.generate_A_Token(user), TokenHelper.generate_R_Token(user));
				newToken.enable = true;
				newToken.save();
				newToken.refresh();
				renderJSON("{\"code\": 200, \"token\": \"" + newToken.a_token + "\", \"refresh_token\": \""+ newToken.r_token +"\", \"updated\": \""+ newToken.updated_at +"\"}");
				
			}
		}
		
	}
	
	@Unrestricted
	public static void logout() {
		LoggerHelper.api_info_Logger("API LOGOUT");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}	
		
		if(appToken != null){
			if (appToken.enable){
				appToken.enable = false;
				appToken.save();
			}
		}
		renderJSON("{\"code\": 200, \"message\": \"Success \"}");
		
	}
	
	@Unrestricted
	public static void refreshToken() {
		LoggerHelper.api_info_Logger("API Refresh Token");

		AppToken appToken = TokenHelper.authenticateApi(request.current());
		if(appToken == null){
			forbidden();
		}
		
		appToken.a_token = TokenHelper.refresh_A_Token(appToken);
		appToken.r_token = TokenHelper.refresh_R_Token(appToken);
		appToken.enable = true;
		appToken.save();
		appToken.refresh();
		
		renderJSON("{\"code\": 200, \"token\": \"" + appToken.a_token + "\", \"refresh_token\": \""+ appToken.r_token +"\", \"updated\": \""+ appToken.updated_at +"\"}");
	}
}
