package com.chinacreator.browser.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtill {
	
	
	
	/**
	 * 验证是否是URL
	 * @param url
	 * @return
	 */
	public static boolean verifyUrl(String url){
		
	    // URL验证规则
	    String regEx ="[a-zA-z]+://[^\\s]*";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(url);
	    boolean rs = matcher.matches();
	    return rs;
		
	}
	

}