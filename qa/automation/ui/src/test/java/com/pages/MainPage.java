package com.pages;

import com.context.TestContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MainPage extends BasePage {
    public MainPage(TestContext context) {
        super(context);
    }

    @FindBy(xpath = "//a[text()='Alerts']")
    public WebElement buttonAlerts;

    @FindBy(xpath = "(//div/button)[1]")
    public WebElement buttonGoogle;

    public boolean isGoogleButtonDisplayed() {
        return buttonGoogle.isDisplayed();
    }
}
