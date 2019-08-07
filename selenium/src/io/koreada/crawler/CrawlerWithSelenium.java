package io.koreada.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.koreada.util.CommonConst;

public class CrawlerWithSelenium {
	
	public static void main(String [] args) {
		CrawlerWithSelenium tp = new CrawlerWithSelenium();
		tp.crawl();
	}
	
    //WebDriver
    private WebDriver driver;
    
    //크롤링 할 URL
    private String base_url;
    
    @SuppressWarnings("deprecation")
	public CrawlerWithSelenium() {
        //System Property SetUp
        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.WEBDRIVER_PATH);
        
        //Driver SetUp
         ChromeOptions options = new ChromeOptions();
         options.setCapability("ignoreProtectedModeSettings", true);
//         options.addArguments("headless");
         
         options.addExtensions(new File("/Users/karlchoi/Downloads/TouchEn-PC보안-확장_v1.0.1.15.crx"));
         DesiredCapabilities capabilities = new DesiredCapabilities();
         capabilities.setCapability(ChromeOptions.CAPABILITY, options);
         driver = new ChromeDriver(capabilities);
         base_url = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp?width=900&height=680&scroll=yes";
    }
 
    public void crawl() {
 
        try {
            driver.get(base_url);
            Thread.sleep(1000);
            
            WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
            WebElement cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
            
            accountElement.click();
            accountElement.sendKeys("05414414001019");
            passElement.click();
            passElement.sendKeys("0409");
            bizNoElement.click();
	        bizNoElement.sendKeys("8101460");
	        cateElement.click();
	        cateElement.sendKeys("1");
	        
	        JavascriptExecutor jse = (JavascriptExecutor)driver;
	        jse.executeScript("setCount2(30)");

	        //	        WebElement noDataElement = driver.findElement(By.xpath(".//*[@class='no_data']"));
	        
            WebElement gridArea = driver.findElement(By.xpath("//div[@id='grid_area']"));
            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(gridArea));
          
            System.out.println(
            				((WebElement)driver.findElement(By.xpath("//*[@id=\"ibk_grid_main\"]/script[2]"))).getAttribute("innerText")
            		);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }
}
