package com.trailmagic.photo;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertNotNull;

/**
 * Created by: oliver on Date: Oct 3, 2010 Time: 2:20:17 AM
 */
public class PublicAlbumsTest {
    @Test
    public void testShowsPublicAlbums() {
        WebDriver driver = new FirefoxDriver();
        driver.get("http://localhost:8081/photo/albums/");
        WebElement header = driver.findElement(By.tagName("h1"));
        assertNotNull(header.getText());
    }
}
