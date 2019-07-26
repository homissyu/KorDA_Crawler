package io.koreada.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.koreada.util.CommonConst;

public class DaumLogin {
	public static void main(String[] args) {
		 
		DaumLogin dl = new DaumLogin();
        dl.crawl();
    }
 
    
    //WebDriver
    private WebDriver driver;
    
    private WebElement webElement;
    
    //크롤링 할 URL
    private String base_url;
    
    public DaumLogin() {
    	
        //System Property SetUp
        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.CHROME_DRIVER_PATH);
        
                
        //Driver SetUp
         ChromeOptions options = new ChromeOptions();
         options.setCapability("ignoreProtectedModeSettings", true);
//         options.addArguments("headless");
         driver = new ChromeDriver(options);
        
        base_url = "https://www.daum.net";
        
        
        
    }
 
    public void crawl() {
 
        try {
            //get page (= 브라우저에서 url을 주소창에 넣은 후 request 한 것과 같다)
            driver.get(base_url);
 
            webElement = driver.findElement(By.className("link_login"));
            webElement.click();
            
            webElement = driver.findElement(By.className("login_account"));
            ArrayList tempList = (ArrayList) webElement.findElements(By.tagName("A"));
            webElement = (WebElement) tempList.get(1);
            webElement.click();
            
            webElement = driver.findElement(By.id("id"));
            webElement.sendKeys("");
            
            webElement = driver.findElement(By.id("inputPwd"));
            webElement.sendKeys("");
            
            webElement = driver.findElement(By.id("loginBtn"));
            webElement.submit();
            
            webElement = driver.findElement(By.className("list_basis"));
            tempList = (ArrayList) webElement.findElements(By.tagName("A"));
            webElement = (WebElement) tempList.get(0);
            webElement.click();
            
            Thread.sleep(1000);
            
            HashMap tempMap = new HashMap();
            
            webElement = driver.findElement(By.className("list_mail"));
            tempList = (ArrayList) webElement.findElements(By.tagName("LI"));
            
            Iterator it = tempList.iterator();
            
            for(int i=0;i<tempList.size();i++) {
            	webElement = (WebElement) tempList.get(i);
            	String mailFrom = webElement.findElement(By.className("link_from")).getText();
            	String mailSubject = webElement.findElement(By.className("tit_subject")).getText();
            	tempMap.put(mailFrom, mailSubject);
            }
            
            System.out.println(tempMap);
            
        } catch (Exception e) {
            
            e.printStackTrace();
        
        } finally {
 
            driver.close();
        }
 
    }
}
