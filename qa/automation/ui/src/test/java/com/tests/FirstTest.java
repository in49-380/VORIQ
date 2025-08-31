package com.tests;

import com.pages.CookiesPage;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstTest extends TestBase {

    @Test
    @Story("First Test")
    @Description("Accepting all cookies")
    public void acceptingAllCookies() {
        assertTrue(new CookiesPage(context).acceptAllCookies().isGoogleButtonDisplayed());
    }
}
