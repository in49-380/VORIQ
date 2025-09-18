import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.example.CarResolve;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;


public class BaseHomeWorkTest {
    protected static Properties properties;

    static Integer idCar;
    static Integer idBrand;
    static Integer idModel;
    static Integer idYear;
    static Integer idEngine;
    static Integer idTransmission;
    static Integer idWheelDrive;


    @BeforeAll
    public static void globalSetUp() {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/config_homework.properties");
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = getConfig("baseURIVoriq");
        RestAssured.filters(new AllureRestAssured());
    }

    public static String getConfig(String key) {
        return properties.getProperty(key);
    }

    @AfterEach
    public void tearDown() {
        RestAssured.reset();
    }

    public static int random0toN(int n) {
        if (n < 0) throw new IllegalArgumentException("n должно быть >= 0");
        return ThreadLocalRandom.current().nextInt(n + 1);
    }
}