package com.trailmagic.photo.test;

/**
 * Created by: oliver on Date: 12/18/10 Time: 6:43 PM
 */
public class WebConstants {
    public static final String BASE_URL = System.getProperty("testing.baseUrl", "http://localhost:8080/photo");
    public static final String SECURE_BASE_URL = System.getProperty("testing.secureBaseUrl", "https://localhost:8443/photo");
    public static final String UPLOADS_URL = BASE_URL + "/rolls/tester/uploads/";

}
