package com.pages;

import com.context.TestContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CookiesPage extends BasePage {
    public CookiesPage(TestContext context) {
        super(context);
    }


    @FindBy(xpath = "//button[text()='Accept all']")
    public WebElement buttonAlerts;

    public MainPage acceptAllCookies() {
        buttonAlerts.click();
        return new MainPage(context);
    }

}
