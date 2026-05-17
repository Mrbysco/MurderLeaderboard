package com.mrbysco.murderleaderboard.util;

/**
 * Utility class for sanitizing usernames to ensure they are safe to construct ResolvableProfile instances with.
 */
public class UsernameUtil {
	public static String getSafeUsername(String username) {
		username = username.replaceAll("[^a-zA-Z0-9_]", "");
		if (username.length() < 2) {
			return "invalid";
		}
		if (username.length() > 16) {
			return username.substring(0, 16);
		}
		return username;
	}
}
