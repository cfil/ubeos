package helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZipCodeHelper {

	/*
	 * Valid characters for zipcodes
	 */
	public static final List<Character> valids = Arrays.asList('0', '1', '2','3','4','5','6','7','8','9','-');
	
	
	/*
	 * This method receives a string and checks if it's a valid zipcode
	 */
	public static boolean isValidZip(String zip){
		int n = zip.length();
		for (int i = 0; i < n; ++i) {
		    char c = zip.charAt(i);
		    if(!valids.contains(c)){
		    	return false;
		    }
		}
		return true;
	}
	
}
