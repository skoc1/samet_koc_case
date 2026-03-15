package com.insider.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By navbar = By.id("navigation");
    private final By hero = By.cssSelector("[class*='hero'], #hero-section");
    private final By footer = By.tagName("footer");


    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("https://insiderone.com/");
        acceptCookies();
    }


    public boolean isOpened() {
        String url = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();
        return url.contains("insiderone.com") || title.contains("insider");
    }

    public boolean isNavbarVisible() {
        return isDisplayed(navbar);
    }

    public boolean isHeroVisible() {
        return isDisplayed(hero);
    }

    public boolean isFooterVisible() {
        return isDisplayed(footer);
    }
}
