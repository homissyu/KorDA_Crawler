package io.koreada.selenium;

import io.koreada.util.CommonConst;

public class SeleniumTest {
 
    public static void main(String[] args) {
 
        SeleniumTest selTest = new SeleniumTest();
        selTest.crawl();
        
    }
 
    
    //WebDriver
    private org.openqa.selenium.WebDriver driver;
    
    //크롤링 할 URL
    private String base_url;
    
    public SeleniumTest() {
        super();
 
        //System Property SetUp
        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.WEBDRIVER_PATH);
        
        //Driver SetUp
        driver = new org.openqa.selenium.chrome.ChromeDriver();
        base_url = "https://www.naver.com";
    }
 
    public void crawl() {
 
        try {
            //get page (= 브라우저에서 url을 주소창에 넣은 후 request 한 것과 같다)
            driver.get(base_url);
            System.out.println(driver.getPageSource());
    
        } catch (Exception e) {
            
            e.printStackTrace();
        
        } finally {
 
            driver.close();
        }
 
    }
 
}