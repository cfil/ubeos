package helpers;

import java.util.List;

import models.Consumer;
import models.MessagesRec;
import models.Proposal;
import models.Provider;
import models.RequestState;
import models.TravelRequest;
import models.User;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Util;

public class TemplatesHelper {

	private static final boolean has_lastminute = false;
	
	@Util
	public static boolean hasLastMinute(){
		return has_lastminute;
	}
	
	@Util
	public static boolean isOutsideTemplate(){

		try {
			if( Response.current().status == 500  ||
				Response.current().status == 404  ||
				Request.current().actionMethod.contains("authenticate")  ||
				Request.current().actionMethod.contains("login")  ||
				Request.current().action.contains("Consumers.create")  ||
				Request.current().action.contains("Consumers.save")  ||
				Request.current().action.contains("Providers.save")  ||
				Request.current().action.contains("Application.agencybase")  ||
				Request.current().actionMethod.contains("recoverPassword")  ||
				Request.current().actionMethod.contains("recoverPasswordForm")  ||
				Request.current().actionMethod.contains("createNew")  ||
				Request.current().actionMethod.contains("aboutUs")  ||
				Request.current().actionMethod.contains("base")  ||
				Request.current().actionMethod.contains("contactUs")  ||
				Request.current().actionMethod.contains("faqs")  ||
				Request.current().actionMethod.contains("media")  ||
				Request.current().actionMethod.contains("privacyEN")  ||
				Request.current().actionMethod.contains("privacyPT")  ||
				Request.current().actionMethod.contains("termsEN")  ||
				Request.current().actionMethod.contains("termsPT") ||
				Request.current().actionMethod.contains("notfoundpage") ||
				Request.current().actionMethod.contains("lastMinute") ||
				Request.current().actionMethod.contains("agencybase") ||
				Request.current().actionMethod.contains("internalerrorpage") ){
				
					return true;
			} else{
				return false;
			}
		}catch (NullPointerException e) {
			return false;
		}
	}
	

}
