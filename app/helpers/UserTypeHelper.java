package helpers;

import models.User;
import models.UserRole;
import play.mvc.Scope.Session;

public class UserTypeHelper {

	/*
	 * This method receives a session and checks if the logged user is ADMIN
	 */
	public static boolean isAdmin(Session session) {
		User user = getLoggedUser(session);
		if (user != null) {
			return user.userRole.name.equals(UserRole.ADMIN);
		} else {
			return false;
		}
	}

	/*
	 * This method receives a session and checks if the logged user is CONSUMER
	 */
	public static boolean isConsumer(Session session) {
		User user = getLoggedUser(session);
		if (user != null) {
			return user.userRole.name.equals(UserRole.CONSUMER);
		} else {
			return false;
		}
	}

	/*
	 * This method receives a session and checks if the logged user is PROVIDER
	 */
	public static boolean isProvider(Session session) {
		User user = getLoggedUser(session);
		if (user != null) {
			return user.userRole.name.equals(UserRole.PROVIDER);
		} else {
			return false;
		}
	}
		
	/*
	 * This method receives a session and returns the logged user
	 */
	public static User getLoggedUser(Session session) {
		if(session.get("ubeos_user") != null){
			return User.findById(Long.parseLong(session.get("ubeos_user")));
		} else {
			return null;
		}
	}

}
