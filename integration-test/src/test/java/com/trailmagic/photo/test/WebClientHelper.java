package com.trailmagic.photo.test;

import com.trailmagic.webclient.WebResponse;
import com.trailmagic.webclient.WebserviceClient;
import com.trailmagic.webclient.http.EntityContentProcessor;
import com.trailmagic.webclient.http.HttpFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by: oliver on Date: Dec 7, 2010 Time: 6:58:50 PM
 */
public class WebClientHelper {
    private WebserviceClient webserviceClient;
    private String baseUrl;

    public WebClientHelper(String baseUrl) {
        this.baseUrl = baseUrl;
        webserviceClient = new WebserviceClient(initHttpClient(), new HttpFactory());
    }

    private DefaultHttpClient initHttpClient() {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            KeyStore trustStore = KeyStore.getInstance("JKS");
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("client.truststore");
            try {
                trustStore.load(inputStream, "password".toCharArray());
            } finally {
                inputStream.close();
            }
            final SSLSocketFactory sslSocketFactory = new SSLSocketFactory(trustStore);
            sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));


            final BasicHttpParams httpParams = new BasicHttpParams();
            return new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not initialize WebClientHelper", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize WebClientHelper", e);
        }
    }

    public void login() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("j_username", "tester");
        paramMap.put("j_password", "password");
        webserviceClient.get(baseUrl + "/photo/rolls/", new NoOpEntityContentProcessor());
        final WebResponse response = webserviceClient.post("https://localhost:8443/photo/j_spring_security_check", paramMap);

        assertEquals(302, response.getStatusCode());
        assertFalse(response.isRedirected());
        assertFalse(response.getFinalUrl().contains("login"));

    }

    public WebserviceClient getWebserviceClient() {
        return webserviceClient;
    }

    private static class NoOpEntityContentProcessor implements EntityContentProcessor<Object> {
        @Override
        public Object process(Reader content) throws Exception {
            return null;
        }
    }


}
