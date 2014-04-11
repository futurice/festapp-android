package com.futurice.festapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.futurice.festapp.ContextRetriever;

public class UrlConfiguration {
	
	private static String CONFIG_FILE = "urlsconfig.cfg";
	
	private static UrlConfiguration instance;
	private Properties properties;
	
	private UrlConfiguration(){
		properties = new Properties();
		InputStream is = null; 
		try {
			is = ContextRetriever.getContext().getAssets().open(CONFIG_FILE);
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static UrlConfiguration getInstance(){
		
		if(instance == null){
			instance = new UrlConfiguration();
		}
		
		return instance;
	}
	
	public String getUrl(String propertyName){
		return properties.getProperty(propertyName);
	}
}
