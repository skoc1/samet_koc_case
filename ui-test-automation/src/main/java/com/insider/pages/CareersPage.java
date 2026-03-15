package com.insider.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CareersPage extends BasePage {

    // Insider careers page
    private final By seeAllTeamsBtn = By.cssSelector("a.see-more");
    private final By seeAllQaJobsBtn = By.cssSelector("div[data-department='Quality Assurance'] a.insiderone-icon-cards-grid-item-btn");

    // Lever page - filters
    private final By locationFilterBtn = By.cssSelector("div[aria-label='Filter by Location: All']");
    private final By departmentFilterBtn = By.cssSelector("div[aria-label='Filter by Team: Quality Assurance']");

    // Lever page - job list
    private final By jobPostings = By.cssSelector("div.posting");
    private final By jobTitle = By.cssSelector("h5[data-qa='posting-name']");
    private final By jobLocation = By.cssSelector("span.sort-by-location");
    private final By jobDepartment = By.cssSelector("div.posting-category-title");
    private final By applyBtn = By.cssSelector("a.posting-btn-submit");

    public CareersPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("https://insiderone.com/careers/#open-roles");
        acceptCookies();
    }

    public void clickSeeAllTeams() {
        acceptCookies();
        WebElement seeAll = waitAndFind(seeAllTeamsBtn);
        scrollTo(seeAll);
        pause();
        jsClick(seeAll);
        pause();
    }

    public void clickQaJobs() {
        WebElement qaBtn = waitAndFind(seeAllQaJobsBtn);
        scrollTo(qaBtn);
        pause();
        jsClick(qaBtn);
        wait.until(ExpectedConditions.urlContains("lever.co"));
        pause();
    }

    public void selectLocation(String location) {
        WebElement wrapper = waitClickable(locationFilterBtn);
        jsClick(wrapper);
        wait.until(ExpectedConditions.attributeToBe(locationFilterBtn, "aria-expanded", "true"));
        pause();

        List<WebElement> options = wrapper.findElements(By.cssSelector(".filter-popup li a.category-link"));
        for (WebElement opt : options) {
            if (opt.getText().trim().contains(location)) {
                jsClick(opt);
                pause();
                return;
            }
        }
        throw new RuntimeException("Location not found: " + location);
    }

    public void selectDepartment(String department) {
        WebElement wrapper = waitClickable(departmentFilterBtn);
        jsClick(wrapper);
        wait.until(ExpectedConditions.attributeToBe(departmentFilterBtn, "aria-expanded", "true"));
        pause();

        List<WebElement> options = wrapper.findElements(By.cssSelector(".filter-popup li a.category-link"));
        for (WebElement opt : options) {
            if (opt.getText().trim().contains(department)) {
                jsClick(opt);
                pause();
                return;
            }
        }
        throw new RuntimeException("Department not found: " + department);
    }

    public boolean hasJobs() {
        return !waitAllVisible(jobPostings).isEmpty();
    }

    public List<String> getPositions() {
        waitAllVisible(jobPostings);

        List<WebElement> elements = driver.findElements(jobTitle);
        List<String> positions = new ArrayList<>();

        for (WebElement e : elements) {
            positions.add(e.getText().trim().toLowerCase());
        }

        return positions;
    }

    public String getDepartmentText() {
        waitAllVisible(jobPostings);
        return driver.findElement(By.cssSelector("div.posting-category-title")).getText().trim().toLowerCase();
    }

    public List<String> getLocations() {
        waitAllVisible(jobPostings);

        List<WebElement> elements = driver.findElements(jobLocation);
        List<String> locations = new ArrayList<>();

        for (WebElement e : elements) {
            locations.add(e.getText().trim());
        }

        return locations;
    }


    public void clickViewRole() {
        List<WebElement> jobs = waitAllVisible(jobPostings);
        WebElement firstJob = jobs.get(0);
        scrollTo(firstJob);

        WebElement apply = firstJob.findElement(applyBtn);
        jsClick(apply);

        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(current)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    public boolean isOnLeverApplicationPage() {
        pause();
        return driver.getCurrentUrl().contains("lever.co") && driver.getPageSource().contains("apply for this job");
    }

    private void pause() {
        try { Thread.sleep(3000); } catch (InterruptedException ignored) { }
    }
}
