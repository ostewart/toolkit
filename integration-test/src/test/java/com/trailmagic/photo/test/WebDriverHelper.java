package com.trailmagic.photo.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by: oliver on Date: 12/18/10 Time: 6:42 PM
 */
public class WebDriverHelper {
    private WebDriver driver;

    public WebDriverHelper(WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        driver.get(WebConstants.UPLOADS_URL);

        driver.get(WebConstants.SECURE_BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("tester");
        final WebElement passwordElement = driver.findElement(By.id("password"));
        passwordElement.sendKeys("password");
        passwordElement.submit();
    }

}
