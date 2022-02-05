import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        //WebDriver driver = new MyFirefoxDriver().getFirefoxDriver();
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.google.com");
        WebElement element = driver.findElement(By.xpath("//input[@title='Search']"));
        element.sendKeys("firefox not launching with fat jar");
        Thread.sleep(3000);
        driver.findElements(By.xpath("//input[@name='btnK']")).get(1).click();

    }
}

