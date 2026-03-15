# Insider QA Test Automation

UI test automation for Insider careers page. Java + Selenium + TestNG with Page Object Model.

## Test Steps

1. Open https://insiderone.com/ and verify home page is loaded (navbar, hero, footer)
2. Go to https://insiderone.com/careers/quality-assurance/, click "See all QA jobs", filter by Istanbul, Turkey and Quality Assurance, verify job list
3. Check all jobs contain "Quality Assurance" in position and department, "Istanbul, Turkey" in location
4. Click "View Role" and verify redirect to Lever application form


## Run

```bash
mvn clean test                    # chrome
mvn clean test -Dbrowser=firefox  # firefox
```

## Requirements

- Java 11+
- Maven 3.8+
- Chrome or Firefox
- Selenium

Screenshots are saved automatically on failure under `screenshots/`.
