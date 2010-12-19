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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ControllerWiringIntegrationTest {
    private WebDriver driver;
    private WebClientHelper client;
    private WebDriverHelper driverHelper;

    @Before
    public void setUp() {
        driver = new FirefoxDriver("WebDriver");
        client = new WebClientHelper(BASE_URL);
        driverHelper = new WebDriverHelper(driver);

        client.login();
        client.uploadTestImage();
        driverHelper.login();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void testShowsPublicAlbumsOwners() {
        driver.get(BASE_URL + "/albums/");
        WebElement header = driver.findElement(By.tagName("h1"));
        assertNotNull(header.getText());
    }

    @Test
    public void testCanLoadRollsOwners() {
        driver.get(BASE_URL + "/rolls/");
        WebElement header = driver.findElement(By.tagName("h1"));
        assertNotNull(header.getText());
    }

    @Test
    public void testCanLoadTesterRolls() {
        driver.get(BASE_URL + "/rolls/tester/");
        WebElement header = driver.findElement(By.tagName("h2"));
        assertEquals("Rolls for User: tester", header.getText());
    }

    @Test
    public void testCanLoadTesterUploads() {
        driver.get(BASE_URL + "/rolls/tester/uploads/");
        WebElement header = driver.findElement(By.className("displayName"));
        assertEquals("Uploads", header.getText());
    }

    @Test
    public void testRollsRedirectsToDirectoryUrl() {
    }
}
