package utils;

public class StringUtils {
	/**
	 * Check if a String is null or empty (the length is null).
	 *
	 * @param s the string to check
	 * @return true if it is null or empty
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}
}
