package io.koreada.selenium;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.phantomjs.PhantomJSDriver;

import io.koreada.util.CommonConst;
import io.koreada.util.Install;

public class PhantomJsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PhantomJsTest pjt = new PhantomJsTest();
		Install mInstall = null;
		if (mInstall == null) {
            try {
                mInstall = new Install(args);
            } catch (Exception e) {
            	mInstall = null;
            	System.exit(1);
            }
        }
		
		int iType = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_WEBDRIVER));
    	String mWebDriverID = CommonConst.WEBDRIVER_ID_ARR[iType];
    	String mWebDriverName = CommonConst.WEBDRIVER_STR_ARR[iType];
    	System.setProperty(mWebDriverID, CommonConst.WEBDRIVER_PATH+File.separator+mWebDriverName);
    	
    	System.out.println(iType);
    	
    	System.out.println(System.getProperty("phantomjs.binary.path"));
    	
    	PhantomJSDriver driver = new PhantomJSDriver();
        String baseUrl = "https://www.naver.com";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl);
        System.out.println(driver.getPageSource());
	}

}
