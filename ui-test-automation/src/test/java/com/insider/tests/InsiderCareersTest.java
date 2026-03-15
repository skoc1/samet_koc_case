package com.insider.tests;

import com.insider.pages.CareersPage;
import com.insider.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class InsiderCareersTest extends BaseTest {

    private HomePage homePage;
    private CareersPage careersPage;


    @Test(priority = 1)
    public void testHomePageIsOpened() {
        homePage = new HomePage(driver);
        homePage.open();

        Assert.assertTrue(homePage.isOpened(), "Home page should be opened");
        Assert.assertTrue(homePage.isNavbarVisible(), "Navbar should be visible");
        Assert.assertTrue(homePage.isHeroVisible(), "Hero section should be visible");
        Assert.assertTrue(homePage.isFooterVisible(), "Footer should be visible");
    }


    @Test(priority = 2, dependsOnMethods = "testHomePageIsOpened")
    public void testFilterQaJobs() {
        careersPage = new CareersPage(driver);
        careersPage.open();
        careersPage.clickSeeAllTeams();
        careersPage.clickQaJobs();
        careersPage.selectLocation("Istanbul, Turkiye");
        careersPage.selectDepartment("Quality Assurance");

        Assert.assertTrue(careersPage.hasJobs(), "Job list should not be empty");
    }

    @Test(priority = 3, dependsOnMethods = "testFilterQaJobs")
    public void testJobListContents() {
        List<String> positions = careersPage.getPositions();
        String department = careersPage.getDepartmentText();
        List<String> locations = careersPage.getLocations();

        Assert.assertTrue(department.contains("qualıty assurance"),
                "Department should contain 'Quality Assurance', found: " + department);

        for (int i = 0; i < positions.size(); i++) {
            Assert.assertTrue(positions.get(i).contains("quality assurance"),
                    "Position #" + (i + 1) + " should contain 'Quality Assurance', found: " + positions.get(i));
            Assert.assertTrue(locations.get(i).contains("ISTANBUL, TURKIYE"),
                    "Location #" + (i + 1) + " should contain 'Istanbul, Turkiye', found: " + locations.get(i));
        }
    }

    @Test(priority = 4, dependsOnMethods = "testJobListContents")
    public void testViewRoleRedirectsToLever() {
        careersPage.clickViewRole();
        Assert.assertTrue(careersPage.isOnLeverApplicationPage(), "Should redirect to Lever apply page");
    }
}
