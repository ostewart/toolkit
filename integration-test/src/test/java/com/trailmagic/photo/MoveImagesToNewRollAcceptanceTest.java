package com.trailmagic.photo;

import com.trailmagic.photo.test.WebClientHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.net.URISyntaxException;

public class MoveImagesToNewRollAcceptanceTest {
    public static final String TEST_IMAGE_FILENAME = "test-image.jpg";
    public static final String BASE_URL = System.getProperty("testing.baseUrl", "http://localhost:8080/photo");
    public static final String SECURE_BASE_URL = System.getProperty("testing.secureBaseUrl", "https://localhost:8443/photo");
    public static final String UPLOADS_URL = BASE_URL + "/rolls/tester/uploads/";
    private WebClientHelper client;
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new FirefoxDriver("WebDriver");
        client = new WebClientHelper(BASE_URL);
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void testCanMoveImages() throws Exception {
        client.login();
        uploadSomeImages();
        final WebElement createRollLink = getUploadsInDisplayStateAndFindCreateLink();
        getUploadsInCreateNewRollState(createRollLink);
        selectImagesAndSubmit();
        verifyCorrectImagesAppearOnNewRollPage();
    }

    private void verifyCorrectImagesAppearOnNewRollPage() {

    }

    private void selectImagesAndSubmit() {

    }

    private void getUploadsInCreateNewRollState(WebElement createRollLink) {
        createRollLink.click();
    }

    private WebElement getUploadsInDisplayStateAndFindCreateLink() {
        driver.get(UPLOADS_URL);
        
        driver.get(SECURE_BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("tester");
        final WebElement passwordElement = driver.findElement(By.id("password"));
        passwordElement.sendKeys("password");
        passwordElement.submit();

        driver.get(UPLOADS_URL);

        return driver.findElement(By.xpath("//a[@rel='createRoll']"));
    }

    private void uploadSomeImages() throws URISyntaxException {
        File imageFile = new File(ClassLoader.getSystemResource(TEST_IMAGE_FILENAME).toURI());
        final String originalUrl = client.uploadImage(imageFile);
    }
}
