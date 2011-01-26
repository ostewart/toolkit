package com.trailmagic.photo;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertTrue;

public class JavascriptTest {
    private WebDriver driver;

    @Test
    public void runJavascriptTestHarness() {
        driver = new FirefoxDriver("WebDriver");
        driver.get("http://localhost:8081/photo/test-runner.html");


        waitUntilTestsFinish();
        assertNoFailures();
        driver.close();
    }

    private void assertNoFailures() {
        final WebElement runner = driver.findElement(By.className("runner"));
        assertTrue(runner.getAttribute("class").contains("passed"));
    }

    private void waitUntilTestsFinish() {
        long beginTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < beginTime + 10 * 1000) {
            try {
                WebElement finished = driver.findElement(By.className("finished-at"));
                if (finished != null && ((RenderedWebElement) finished).isDisplayed()) {
                    return;
                }
            } catch (NoSuchElementException e) {
                // expected...thanks for testing your own examples, webdriver
            }
        }
    }
}
