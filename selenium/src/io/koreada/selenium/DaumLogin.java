package io.koreada.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    //�겕濡ㅻ쭅 �븷 URL
    private String base_url;
    
    public DaumLogin() {
    	int iType = 0;
    	String mWebDriverID = CommonConst.WEBDRIVER_ID_ARR[iType];
    	String mWebDriverName = CommonConst.WEBDRIVER_STR_ARR[iType];
    	if(CommonConst.getOSName().equalsIgnoreCase("Windows")) mWebDriverName += ".exe";
    	System.setProperty(mWebDriverID, CommonConst.WEBDRIVER_PATH + File.separator + mWebDriverName);
    	System.out.println(CommonConst.WEBDRIVER_PATH + File.separator + mWebDriverName);
        //System Property SetUp
//        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.WEBDRIVER_PATH);
        
                
        //Driver SetUp
         ChromeOptions options = new ChromeOptions();
         options.setCapability("ignoreProtectedModeSettings", true);
//         options.addArguments("headless");
         driver = new ChromeDriver(options);
        
        base_url = "https://www.daum.net";
        
        
        
    }
 
    public void crawl() {
 
        try {
            //get page (= 釉뚮씪�슦���뿉�꽌 url�쓣 二쇱냼李쎌뿉 �꽔�� �썑 request �븳 寃껉낵 媛숇떎)
            driver.get(base_url);
 
            webElement = driver.findElement(By.className("link_login"));
            webElement.click();
            
            webElement = driver.findElement(By.className("login_account"));
            ArrayList<?> tempList = (ArrayList<?>) webElement.findElements(By.tagName("A"));
            webElement = (WebElement) tempList.get(1);
            webElement.click();
            
            webElement = driver.findElement(By.id("id"));
            webElement.sendKeys("");
            
            webElement = driver.findElement(By.id("inputPwd"));
            webElement.sendKeys("");
            
            webElement = driver.findElement(By.id("loginBtn"));
            webElement.submit();
            
            webElement = driver.findElement(By.className("list_basis"));
            tempList = (ArrayList<?>) webElement.findElements(By.tagName("A"));
            webElement = (WebElement) tempList.get(0);
            webElement.click();
            
            Thread.sleep(1000);
            
            HashMap<String, String> tempMap = new HashMap<String, String>();
            
            webElement = driver.findElement(By.className("list_mail"));
            tempList = (ArrayList<?>) webElement.findElements(By.tagName("LI"));
            
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
 
//            driver.close();
        }
 
    }
}
