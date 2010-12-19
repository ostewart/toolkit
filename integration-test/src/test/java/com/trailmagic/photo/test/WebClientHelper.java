package com.trailmagic.photo.test;

import com.trailmagic.webclient.WebResponse;
import com.trailmagic.webclient.WebserviceClient;
import com.trailmagic.webclient.XPathEntityContentProcessor;
import com.trailmagic.webclient.http.EntityContentProcessor;
import com.trailmagic.webclient.http.HttpFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by: oliver on Date: Dec 7, 2010 Time: 6:58:50 PM
 */
public class WebClientHelper {
    private WebserviceClient webserviceClient;
    private String baseUrl;
    private static final Logger log = LoggerFactory.getLogger(WebClientHelper.class);
    public static final String TEST_IMAGE_FILENAME = "test-image.jpg";


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
        webserviceClient.get(baseUrl + "/rolls/", new NoOpEntityContentProcessor());
        final WebResponse response = webserviceClient.post("https://localhost:8443/photo/j_spring_security_check", paramMap);

        assertEquals(302, response.getStatusCode());
        assertFalse(response.isRedirected());
        assertFalse(response.getFinalUrl().contains("login"));
    }

    public String uploadImage(File imageFile) {
        final WebResponse response = webserviceClient.postFile(baseUrl + "/upload/" + "?fileName=" + imageFile.getName(),
                                                               imageFile, "application/octet-stream");
        log.debug("Upload response: {}", response.toString());

        assertTrue("Response is a redirect", response.isRedirected());
        assertTrue("Redirects to uploads roll URL (actual: " + response.getFinalUrl() + ")", response.getFinalUrl().startsWith(baseUrl + "/rolls/tester/uploads/"));

        return webserviceClient.get(response.getFinalUrl(), new OriginalImageLinkEntityContentProcessor());
    }

    public String uploadTestImage() {
        File imageFile = null;
        try {
            imageFile = new File(ClassLoader.getSystemResource(TEST_IMAGE_FILENAME).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't open test image", e);
        }
        return uploadImage(imageFile);
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

    private static class OriginalImageLinkEntityContentProcessor extends XPathEntityContentProcessor<String> {
        @Override
        public String processWithDocument(Document document) throws Exception {
            XPath xPath = XPath.newInstance("//html:a[contains(text(),'orig')]");
            xPath.addNamespace("html", "http://www.w3.org/1999/xhtml");

            final Element aElement = (Element) xPath.selectSingleNode(document);
            return aElement.getAttribute("href").getValue();
        }
    }

}
