package com.tests;

import com.context.TestContext;
import com.utils.ConfigurationReader;
import com.utils.DriverFactory;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestBase {
    TestContext context;

    @BeforeEach
    public void beforeMethod() {
        context = new TestContext();
        context.driver = DriverFactory.get();
        context.wait = new WebDriverWait(context.driver, Duration.ofSeconds(Long.parseLong(ConfigurationReader.get("timeout"))));
        context.actions = new Actions(context.driver);
        context.js = (JavascriptExecutor) context.driver;
        context.driver.get(ConfigurationReader.get("base_url"));
        context.logs = new StringBuilder();
    }

    @AfterEach
    public void afterMethod() {
        Allure.addAttachment("Console log: ", String.valueOf(context.logs));
        Allure.getLifecycle().addAttachment(
                "Screenshot", "image/png", "png",
                ((TakesScreenshot) context.driver).getScreenshotAs(OutputType.BYTES)
        );
        if (context.driver != null) {
            context.driver.quit();
        }
    }
}