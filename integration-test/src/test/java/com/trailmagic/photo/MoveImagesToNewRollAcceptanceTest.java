package com.trailmagic.photo;

import com.trailmagic.photo.test.WebClientHelper;
import com.trailmagic.photo.test.WebDriverHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.trailmagic.photo.test.WebConstants.BASE_URL;
import static com.trailmagic.photo.test.WebConstants.UPLOADS_URL;

public class MoveImagesToNewRollAcceptanceTest {
    private WebClientHelper client;
    private WebDriver driver;
    private WebDriverHelper driverHelper;

    @Before
    public void setUp() {
        driver = new FirefoxDriver("WebDriver");
        driverHelper = new WebDriverHelper(driver);
        client = new WebClientHelper(BASE_URL);
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    @Ignore
    public void testCanMoveImages() throws Exception {
        client.login();
        String imageLink = client.uploadTestImage();
        getUploadsInDisplayStateAndFindCreateLink();

        getUploadsInCreateNewRollState(findCreateRollLink());
        selectImage(imageLink);
        enterRollName("Test Roll");
        driver.findElement(By.id("submitCreateRoll")).submit();

        verifyCorrectImagesAppearOnNewRollPage();
        verifyMovedImagesNoLongerAppearOnUploadsPage();
    }

    private void verifyMovedImagesNoLongerAppearOnUploadsPage() {

    }

    private void enterRollName(String rollName) {
        WebElement rollNameInput = driver.findElement(By.id("rollName"));
        rollNameInput.sendKeys(rollName);
    }

    private WebElement findCreateRollLink() {
        return driver.findElement(By.xpath("//a[@rel='createRoll']"));
    }

    private void verifyCorrectImagesAppearOnNewRollPage() {

    }


    private void selectImage(String imageLink) {
        String path = getPathFromUrl(imageLink);
        WebElement imageCheckbox = driver.findElement(By.xpath("//li[@id='" + path + "']/input"));
        imageCheckbox.click();
    }

    private String getPathFromUrl(String imageLink) {
        try {
            return new URL(imageLink).getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Couldn't extract path from URL", e);
        }
    }

    private void getUploadsInCreateNewRollState(WebElement createRollLink) {
        createRollLink.click();
    }

    private void getUploadsInDisplayStateAndFindCreateLink() {
        driverHelper.login();

        driver.get(UPLOADS_URL);
    }


}
