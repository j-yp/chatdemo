package com.example.chat.config.utils;

import java.net.URLDecoder;

public class UrlDecode {
	
    @SuppressWarnings("deprecation")
	public static String getURLDecode(String filePath) {
        return URLDecoder.decode(filePath);
    }
}
