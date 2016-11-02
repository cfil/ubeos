package helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import play.Logger;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;

public class LoggerHelper {

	/*
	 * Method used to Log a DEBUG message to file, receives the Log message
	 */
	public static void debug_Logger(String message){
		
		// Gets the current Session
		Session session = Session.current();
		
		String user = "[user: " + session.get("ubeos_user") + "]" ;
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.debug(user + " " + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a INFO message to file, receives the Log message
	 */
	public static void info_Logger(String message){
		
		// Gets the current Session
		Session session = Session.current();
		
		String user = "[user: " + session.get("ubeos_user") + "]" ;
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.info(user + " " + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a ERROR message to file, receives the Log message
	 */
	public static void error_Logger(String message){
		
		// Gets the current Session
		Session session = Session.current();
		
		String user = "[user: " + session.get("ubeos_user") + "]" ;
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.error(user + " " + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a WARN message to file, receives the Log message
	 */
	public static void warnign_Logger(String message){
		
		// Gets the current Session
		Session session = Session.current();
		
		String user = "[user: " + session.get("ubeos_user") + "]" ;
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.warn(user + " " + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a API DEBUG message to file, receives the Log message
	 */
	public static void api_debug_Logger(String message){
				
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.debug("[API]" + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a API INFO message to file, receives the Log message
	 */
	public static void api_info_Logger(String message){
		
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.info("[API]" + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a API ERROR message to file, receives the Log message
	 */
	public static void api_error_Logger(String message){
		
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.error("[API]" + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	
	/*
	 * Method used to Log a API WARN message to file, receives the Log message
	 */
	public static void api_warnign_Logger(String message){
		
		String actionMethod = "[" + Request.current().action + "]" ;
		Logger.warn("[API]" + actionMethod + " " + "["+message+"]" + " " + "["+Request.current().remoteAddress+"] ");
	}
	

}
