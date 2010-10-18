package com.trailmagic.photo;

import com.trailmagic.webclient.WebResponse;
import com.trailmagic.webclient.WebserviceClient;
import com.trailmagic.webclient.XPathEntityContentProcessor;
import com.trailmagic.webclient.http.EntityContentProcessor;
import com.trailmagic.webclient.http.HttpFactory;
import org.apache.commons.io.IOUtils;
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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleImageUploadIntegrationTest {
    private static final String HOST_PORT_URL = "http://localhost:8081";
    private static final String CONTEXT_URL = HOST_PORT_URL + "/photo";
    private static final String BASE_URL = CONTEXT_URL + "/upload";
    private WebserviceClient webserviceClient;
    private File imageFile;

    @Before
    public void setUp() throws Exception {
        imageFile = new File(ClassLoader.getSystemResource("test-image.jpg").toURI());
        webserviceClient = new WebserviceClient(initHttpClient(), new HttpFactory());
    }

    private DefaultHttpClient initHttpClient() {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            KeyStore trustStore = KeyStore.getInstance("JKS");
            final FileInputStream inputStream = new FileInputStream("/tmp/server.truststore");
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
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("didn't work");
    }


    @Test
    public void testImageUpload() throws URISyntaxException {
        login();
        final WebResponse response = webserviceClient.postFile(BASE_URL, imageFile, "application/octet-stream");

        System.out.println(response);

        assertTrue("Response is a redirect", response.isRedirected());
        assertTrue("Redirects to uploads roll URL (actual: " + response.getFinalUrl() + ")", response.getFinalUrl().startsWith(CONTEXT_URL + "/rolls/tester/uploads/"));


        final String origUrl = makeAbsoluteUrl(response.getFinalUrl(), webserviceClient.get(response.getFinalUrl(), new XPathEntityContentProcessor<String>() {
            @Override
            public String processWithDocument(Document document) throws Exception {
                XPath xPath = XPath.newInstance("//html:a[contains(text(),'orig')]");
                xPath.addNamespace("html", "http://www.w3.org/1999/xhtml");

                final Element aElement = (Element) xPath.selectSingleNode(document);
                return aElement.getAttribute("href").getValue();
            }
        }));

        String imageUrl = makeAbsoluteUrl(HOST_PORT_URL, webserviceClient.get(origUrl, new XPathEntityContentProcessor<String>() {
            @Override
            public String processWithDocument(Document document) throws Exception {
                XPath xPath = XPath.newInstance("//html:img");
                xPath.addNamespace("html", "http://www.w3.org/1999/xhtml");

                final Element aElement = (Element) xPath.selectSingleNode(document);
                return aElement.getAttribute("src").getValue();
            }
        }));


        assertTrue(webserviceClient.get(imageUrl, new EntityContentProcessor<Boolean>() {
            @Override
            public Boolean process(Reader reader) throws Exception {
                return matchesFile(imageFile, reader);
            }
        }));
    }

    private String makeAbsoluteUrl(String baseUrl, String url) {
        if (url.startsWith("http")) {
            return url;
        }
        return baseUrl + url;
    }

    private void login() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("j_username", "tester");
        paramMap.put("j_password", "password");
        webserviceClient.post(HOST_PORT_URL + "/photo/rolls/", paramMap);
        final WebResponse response = webserviceClient.post("https://localhost:8443/photo/j_spring_security_check", paramMap);
        assertEquals(302, response.getStatusCode());
        assertFalse(response.isRedirected());

    }

    private Boolean matchesFile(File file, Reader reader) throws IOException {
        return IOUtils.contentEquals(new FileReader(file), reader);
    }

}
