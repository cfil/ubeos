package helpers.api;

import java.util.Date;

import models.AppToken;
import models.User;
import play.mvc.Http.Request;
import play.mvc.Util;
import utils.Hashids;

public class TokenHelper {

	private static final String TOKEN = "token";
	private static final String LANG = "lang";
	public static final String PT = "pt";
	public static final String EN = "en";
	
	@Util
	public static AppToken authenticateApi(Request request){
		helpers.LoggerHelper.api_info_Logger("Authenticating API...");
		
		if(!request.headers.containsKey(TOKEN)){
			helpers.LoggerHelper.api_info_Logger("No token on header");
			return null;
		}
		
		String token_request = request.headers.get(TOKEN).value();
		AppToken token = AppToken.findBy_AToken(token_request);
		if(token == null){
			helpers.LoggerHelper.api_info_Logger("No token created");
			return null;
		}
		if(!token.enable){
			helpers.LoggerHelper.api_info_Logger("token expired");
			return null;
		}
		
		return token;
		
	}
	
	@Util
	public static String generate_A_Token(User user) {
		
		Hashids hashids = new Hashids("user authentication token");
		String uuid = hashids.encode(user.id, user.created_at.getTime(), new Date().getTime());
		return uuid;
	}

	@Util
	public static String generate_R_Token(User user) {
		
		Hashids hashids = new Hashids("user refresh token");
		String uuid = hashids.encode(user.id, user.created_at.getTime(), new Date().getTime());
		return uuid;
	}

	@Util
	public static String refresh_A_Token(AppToken appToken) {
		Date now = new Date();
		Hashids hashids = new Hashids("user authentication token refresh");
		String uuid = hashids.encode(appToken.user, appToken.created_at.getTime(), now.getTime());
		return uuid;
	}
	
	@Util
	public static String refresh_R_Token(AppToken appToken) {
		Date now = new Date();
		Hashids hashids = new Hashids("user refresh token refresh");
		String uuid = hashids.encode(appToken.user, appToken.created_at.getTime(), now.getTime());
		return uuid;
	}
	
	@Util
	public static String getApiLang(Request request){
			
		String lang = EN;
		if(request.headers.containsKey(LANG)){
			lang = request.headers.get(LANG).value();
		}
				
		return lang;
		
	}
//	@Util
//	public static String getToken(Request request){
//		return Request.current().headers.get(TOKEN).value();
//	}

}
