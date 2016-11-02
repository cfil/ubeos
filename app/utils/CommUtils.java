package utils;

import play.mvc.Util;

public class CommUtils {
	
	@Util
	public static boolean checkValidMessage(String message){
		if(!StringUtils.isNullOrEmpty(message)){
			if(message.contains("@")) {
				return false;
			}
			if(message.contains("mail")) {
				return false;
			}
			String regexMessage = message.replaceAll("\\s","");
			
			if(regexMessage.matches(".*\\d{9}.*")) {
				return false;
			}
		}
				
		return true;
	}
}
