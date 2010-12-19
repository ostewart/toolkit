package com.trailmagic.photo;

import com.trailmagic.photo.test.WebClientHelper;
import com.trailmagic.photo.test.WebDriverHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

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
    public void testCanMoveImages() throws Exception {
        client.login();
        client.uploadTestImage();
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
        driverHelper.login();

        driver.get(UPLOADS_URL);

        return driver.findElement(By.xpath("//a[@rel='createRoll']"));
    }


}
