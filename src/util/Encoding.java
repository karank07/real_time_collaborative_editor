package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class Encoding {

	public static String encode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public static String decode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;

	}

}
