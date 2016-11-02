package controllers;

import helpers.LoggerHelper;
import helpers.UserTypeHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonObject;

import groovy.ui.SystemOutputInterceptor;
import models.User;
import models.UserRole;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Util;
import play.mvc.With;

public class Security extends Controller {

	public static final String USER_COOKIE = "ubeos_user";
//	private static final String REMEMBERME = "rememberme";
	public static final String ORIGINAL_URL = "originalUrl";
	public static final String ROOT = "/";

	private static final String GET = "GET";
	public static final String USER = "currentUser";

	private static final String SECURITY_ERROR = "controllers.security.error";
	
	public static final String RENDER_ERROR = "renderError";

	public static final String REMEMBER_DURATION = Play.configuration
			.getProperty("application.cookie.remember.duration", "7d");

	@Before(unless = { "login", "authenticate", "logout", "recovery",
			"sendRecovery" })
	public static void checkAccess() throws Throwable {

			// Verify if user is connected - has session
			if (!isConnected()) {
				final String originalUrl = GET.equals(request.method) ? request.url
						: ROOT;
				flash.put(ORIGINAL_URL, originalUrl);
				flash.put(RENDER_ERROR, false);
				login();
			}
			
			if(!request.action.equals("Users.Image")){
			final String originalUrl = GET.equals(request.method) ? request.url
					: ROOT;
			flash.put(ORIGINAL_URL, originalUrl);
		}
	}

	/**
	 * login method
	 * 
	 * @throws Throwable
	 */
	public static void login() {
		LoggerHelper.info_Logger("Login");
		flash.remove(Security.RENDER_ERROR);
		if(UserTypeHelper.getLoggedUser(session) != null){
			LoggerHelper.info_Logger("Already Logged...");
			Application.index();
		}
		
//		Http.Cookie remember = request.cookies.get(REMEMBERME);
//		if (remember != null) {
//			LoggerHelper.info_Logger("TEM REMEMBER ME (step 1)");
//			int firstIndex = remember.value.indexOf("-");
//			String sub = remember.value.substring(firstIndex + 1,
//					remember.value.length());
//			int subdomainIndex = sub.indexOf("-") + firstIndex + 1;
//			int lastIndex = remember.value.lastIndexOf("-");
//
//			if (lastIndex > firstIndex) {
//				LoggerHelper.info_Logger("TEM REMEMBER ME (step 2)");
//				
//				String sign = remember.value.substring(0, firstIndex);
//				String restOfCookie = remember.value.substring(firstIndex + 1);
//				String email = remember.value.substring(firstIndex + 1,
//						subdomainIndex);
//
//				String time = remember.value.substring(lastIndex + 1);
//				Date expirationDate = new Date(Long.parseLong(time));
//				Date now = new Date();
//				if (expirationDate == null || expirationDate.before(now)) {
//					logout();
//				}
//
//				User user = User.findByEmail(email);
//				if (Crypto.sign(restOfCookie).equals(sign)) {
//					
//					LoggerHelper.info_Logger("TEM REMEMBER ME (step 3)");
//					
//					// Mark user as connected
//					session.put(USER_COOKIE, user.id);
//					redirect(flash.get(ORIGINAL_URL) != null ? flash
//							.get(ORIGINAL_URL) : ROOT);
//				}
//			}
//		}

		changeLocaleIfDefined();
		flash.keep(ORIGINAL_URL);
		boolean activeHeaderLogin = true;
		
		render(activeHeaderLogin);			
	}

	/**
	 * authenticate username and password credentials for interface user
	 * 
	 * @param username
	 * @param password
	 * @throws Throwable
	 */
	public static void authenticate(String username, String password,
			boolean remember) throws Throwable {
		validation.required(username);
		validation.required(password);
		flash.keep(ORIGINAL_URL);
		if (validation.hasErrors()) {
//			flash.error(Messages.get(SECURITY_ERROR));
			flash.error(Messages.get("security.required.fields"));
			flash.keep(ORIGINAL_URL);
			flash.put("username", username);
			flash.put(RENDER_ERROR, true);
			render("@login");
		}
		Boolean allowed = true;

		// Verify if username exist on current organization or if the support
		// team
		User user = User.findByEmail(username);

		if (user == null) {
			allowed = false;
		}
		
		if(allowed){
			if(user.blocked && user.userRole == UserRole.findByName(UserRole.PROVIDER)){
				renderTemplate("Application/providerAlert.html");
			}
		}
		
		// Allow only enabled users
		if (allowed && !user.enable) {
			allowed = false;
			flash.error(Messages.get("security.not.active"));
			flash.keep(ORIGINAL_URL);
			flash.put("username", username);
			flash.put(RENDER_ERROR, true);
			render("@login");
		}
		// check user credentials
		allowed = allowed ? user.checkPassword(password) : false;

		if (!allowed) {
			flash.error(Messages.get(SECURITY_ERROR));
			flash.keep(ORIGINAL_URL);
			flash.put("username", username);
			flash.put(RENDER_ERROR, true);
			render("@login");
						
		}

		// at least one role is active and user doesnt have a APP role
		if (allowed) {
			allowed = false;
			List<UserRole> roles = user.getRoles();
			if (roles == null || roles.isEmpty()) {
				Logger.debug("Email %s as no Roles defined!", username);
			} else {
				allowed = true;
			}
		}

		// Language
		// If user has a supported language it uses that one, otherwise use the
		// browser lang
		if (user.lang != null) {
			Lang.change(user.lang);
		}

		// Mark user as connected
		session.put(USER_COOKIE, user.id);
		LoggerHelper.info_Logger(session.current().all().toString());

		onAuthenticated();

		// make the user available in templates
		renderArgs.put(USER, user);

//				flash.success(Messages.get(SECURITY_LOGIN));
		LoggerHelper.info_Logger("Redirect to Orginal URL ---> " + flash.get(ORIGINAL_URL));
		redirect(flash.get(ORIGINAL_URL) != null ? flash.get(ORIGINAL_URL)
				: ROOT);
	}

	/**
	 * logout user session
	 * 
	 * @throws Throwable
	 */
	public static void logout() {
		LoggerHelper.info_Logger("Logout");
		if (isConnected()) {
			onDisconnect();
			session.clear();
			onDisconnected();
//			response.removeCookie(REMEMBERME);
			
			Lang.change(Lang.getLocale().getDisplayName());
		}
		Application.index();
	}

	
	// ~~~~~~~~~~~~~~~~~~~~~ Utils

	/**
	 * Indicate if a user is currently connected
	 * 
	 * @return true if the user is connected
	 */
	public static boolean isConnected() {
		return session.get(USER_COOKIE) != null;
	}

	/**
	 * This method returns the current connected user id
	 */
	public static Long connected() {
		return Long.parseLong(session.get(USER_COOKIE));
	}

	/**
	 * This method returns the current connected user
	 */
	public static User currentUser() {
		// first, try to get it from the renderArgs since it should be there!!.
		if (renderArgs.get(USER) != null) {
			return (User) renderArgs.get(USER);
		} else {
			User user = (User) (isConnected() ? User.findById(connected())
					: null);
			renderArgs.put(USER, user);
			return user;
		}
	}

	/**
	 * This method is called after a successful authentication. (eg. Record the
	 * time the user signed in)
	 */
	static void onAuthenticated() {
	}

	/**
	 * This method is called before a user tries to sign off.(eg. Record the
	 * name of the user who signed off)
	 */
	static void onDisconnect() {
	}

	/**
	 * This method is called after a successful sign off. (eg. Record the time
	 * the user signed off)
	 */
	static void onDisconnected() {
	}

	/**
	 * Change language for next requests based on request locale
	 * 
	 * @return {@link String} language if changed {@code null} otherwise
	 */
	@Util
	public static String changeLocaleIfDefined() {

		String lang = request.params.get("locale");
		if(lang != null) {
			LoggerHelper.info_Logger("change to lang: " + lang);
			Lang.change(lang);
			request.url = request.url.substring(0, request.url.indexOf("locale") - 1);
			LoggerHelper.info_Logger("redirect: " + request.url);
			redirect(request.url);
		}
		return lang;
	}

}