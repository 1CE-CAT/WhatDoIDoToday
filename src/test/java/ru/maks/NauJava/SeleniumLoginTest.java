package ru.maks.NauJava;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SeleniumLoginTest {

    @LocalServerPort
    private int port;
    
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    @Test
    void testLoginAndLogout() {
        driver.get("http://localhost:" + port + "/login");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.tagName("form")).submit();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/view/tasks"), 
                "Не удалось войти, текущий URL: " + driver.getCurrentUrl());
        Assertions.assertTrue(driver.getPageSource().contains("Список задач"), 
                "Страница задач не загрузилась");
        driver.findElement(By.xpath("//input[@value='Выйти из системы']")).click();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), 
                "Выход не удался, текущий URL: " + driver.getCurrentUrl());
    }
}