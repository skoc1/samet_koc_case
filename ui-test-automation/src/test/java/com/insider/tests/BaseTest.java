package com.insider.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;


    @BeforeClass
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");

        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions opts = new FirefoxOptions();
            driver = new FirefoxDriver(opts);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            driver = new ChromeDriver(opts);
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod
    public void screenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    private void takeScreenshot(String testName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dir = Paths.get("screenshots");
            Files.createDirectories(dir);
            Files.copy(src.toPath(), dir.resolve(testName + "_" + System.currentTimeMillis() + ".png"));
        } catch (Exception e) {
            System.err.println("Screenshot failed: " + e.getMessage());
        }
    }
}
