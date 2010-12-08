package com.trailmagic.photo;

import com.trailmagic.photo.test.WebClientHelper;
import com.trailmagic.webclient.WebResponse;
import com.trailmagic.webclient.WebserviceClient;
import com.trailmagic.webclient.XPathEntityContentProcessor;
import com.trailmagic.webclient.http.EntityContentProcessor;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class SimpleImageUploadIntegrationTest {
    private static final String HOST_PORT_URL = "http://localhost:8081";
    private static final String CONTEXT_URL = HOST_PORT_URL + "/photo";
    private static final String BASE_URL = CONTEXT_URL + "/upload";
    private File imageFile;
    private WebserviceClient webserviceClient;
    private static final Logger log = LoggerFactory.getLogger(SimpleImageUploadIntegrationTest.class);
    public static final String TEST_IMAGE_FILENAME = "test-image.jpg";

    @Before
    public void setUp() throws Exception {
        imageFile = new File(ClassLoader.getSystemResource(TEST_IMAGE_FILENAME).toURI());
        WebClientHelper client = new WebClientHelper(HOST_PORT_URL);
        client.login();
        webserviceClient = client.getWebserviceClient();
    }

    @Test
    public void testImageUpload() throws URISyntaxException {
        final WebResponse response = webserviceClient.postFile(BASE_URL + "?fileName=foo.jpg", imageFile, "application/octet-stream");

        log.debug("Upload response: {}", response.toString());

        assertTrue("Response is a redirect", response.isRedirected());
        assertTrue("Redirects to uploads roll URL (actual: " + response.getFinalUrl() + ")", response.getFinalUrl().startsWith(CONTEXT_URL + "/rolls/tester/uploads/"));


        final String originalLink = webserviceClient.get(response.getFinalUrl(), new OriginalImageLinkEntityContentProcessor());
        final String origUrl = makeAbsoluteUrl(response.getFinalUrl(), originalLink);

        final String imageFileLink = webserviceClient.get(origUrl, new SingleImageUrlEntityContentProcessor());
        String imageUrl = makeAbsoluteUrl(HOST_PORT_URL, imageFileLink);

        assertTrue(webserviceClient.get(imageUrl, new FileMatchesEntityContentProcessor(imageFile)));
    }

    private String makeAbsoluteUrl(String baseUrl, String url) {
        if (url.startsWith("http")) {
            return url;
        }
        return baseUrl + url;
    }


    private Boolean matchesFile(File file, Reader reader) throws IOException {
        return IOUtils.contentEquals(new FileReader(file), reader);
    }

    private static class SingleImageUrlEntityContentProcessor extends XPathEntityContentProcessor<String> {
        @Override
        public String processWithDocument(Document document) throws Exception {
            XPath xPath = XPath.newInstance("//html:img");
            xPath.addNamespace("html", "http://www.w3.org/1999/xhtml");

            final Element aElement = (Element) xPath.selectSingleNode(document);
            return aElement.getAttribute("src").getValue();
        }
    }

    private class FileMatchesEntityContentProcessor implements EntityContentProcessor<Boolean> {
        private File file;

        public FileMatchesEntityContentProcessor(File file) {
            this.file = file;
        }

        @Override
        public Boolean process(Reader reader) throws Exception {
            return matchesFile(file, reader);
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
