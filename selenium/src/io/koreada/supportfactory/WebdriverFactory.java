package io.koreada.supportfactory;

import java.io.File;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import io.koreada.util.CommonConst;
import io.koreada.util.Debug;
import io.koreada.util.Install;

public class WebdriverFactory {
	private Install mInstall = null;
	
	//WebDriver
    private DesiredCapabilities capabilities = null;
    private ChromeOptions mChromeOptions = null;
    private FirefoxOptions mFirefoxOptions = null;
    private SafariOptions mSafariOptions = null;
    private EdgeOptions mEdgeOptions = null;
    private InternetExplorerOptions mIEOptions = null;
    
    private int iType = 0;
    
    private String mWebDriverName = null;
    private String mWebDriverID = null;
    
    public WebdriverFactory(Debug aDebug, Install aInstall) {
    	mInstall = aInstall;
//    	this.driver = generateDriver();
    }
    
    public WebDriver getDriver() {
    	return generateDriver();
    }
    
	@SuppressWarnings("deprecation")
	private WebDriver generateDriver() {
    	iType = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_WEBDRIVER));
    	mWebDriverID = CommonConst.WEBDRIVER_ID_ARR[iType];
    	mWebDriverName = CommonConst.WEBDRIVER_STR_ARR[iType];
    	if(CommonConst.getOSName().equalsIgnoreCase("Windows")) mWebDriverName += ".exe";
    	System.setProperty(mWebDriverID, mInstall.getLibDir() + File.separator + CommonConst.WEBDRIVER_PATH + File.separator + mWebDriverName);
    	capabilities = new DesiredCapabilities();
    	WebDriver ret = null;
    	
    	switch(iType) {
	    	case 0:
	    		mChromeOptions = new ChromeOptions();
	        	mChromeOptions.setCapability("ignoreProtectedModeSettings", true);
//	            mChromeOptions.addArguments("headless");
	        	mChromeOptions.addExtensions(new File(mInstall.getLibDir() + File.separator + CommonConst.TOUCH_EN_CHROME_PATH));
	        	capabilities.setCapability(ChromeOptions.CAPABILITY, mChromeOptions);
	        	capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	        	ret = new ChromeDriver(capabilities);
	        	break;
	    	case 1:
	    		mFirefoxOptions = new FirefoxOptions();
	    		mFirefoxOptions.setCapability("ignoreProtectedModeSettings", true);
	    		FirefoxProfile profile = new FirefoxProfile();
	    		
	    		FirefoxBinary firefoxBinary = new FirefoxBinary();
//	    	    firefoxBinary.addCommandLineOptions("--headless");
	    	    mFirefoxOptions.setBinary(firefoxBinary);

	    		profile.setPreference("devtools.toolbox.selectedTool", "netmonitor");
	    		profile.setPreference("devtools.toolbox.footer.height", 0);
	    		profile.setPreference("devtools.devedition.promo.enabled", false);
	    		profile.setPreference("devtools.devedition.promo.shown", false);
	    		profile.addExtension(new File(mInstall.getLibDir() + File.separator + CommonConst.TOUCH_EN_FIREFOX_PATH));
	    		mFirefoxOptions.addArguments("-devtools");
	    		mFirefoxOptions.setProfile(profile);
	    		
	    		capabilities.setCapability(ChromeOptions.CAPABILITY, mFirefoxOptions);
	    		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	    		ret = new FirefoxDriver(capabilities);
	    		break;
	    	case 2:
	    		ret = new PhantomJSDriver();
	    		break;
	    	case 3:
	    		mSafariOptions = new SafariOptions();
	    		mSafariOptions.setCapability("ignoreProtectedModeSettings", true);
	    		capabilities.setCapability(SafariOptions.CAPABILITY, mSafariOptions);
	    		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	    		ret = new SafariDriver(capabilities);
	    		break;
	    	case 4:
	    		mEdgeOptions = new EdgeOptions();
	    		mEdgeOptions.setCapability("ignoreProtectedModeSettings", true);
//	            mChromeOptions.addArguments("headless");
//	    		mEdgeOptions.addExtensions(new File(CommonConst.TOUCH_EN_CHROME_PATH));
	        	capabilities.setCapability(ChromeOptions.CAPABILITY, mChromeOptions);
	        	capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	        	ret = new EdgeDriver(capabilities);
	            break;
	    	case 5:
	    		mIEOptions = new InternetExplorerOptions();
	    		mIEOptions.setCapability("ignoreProtectedModeSettings", true);
	        	capabilities.setCapability(ChromeOptions.CAPABILITY, mIEOptions);
	        	capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	        	capabilities.setCapability("ignoreZoomSetting", true);
//	    		DesiredCapabilities ieCapabilities=DesiredCapabilities.internetExplorer();
//	    		capabilities.setCapability(InternetExplorerDriver
//	    		 .INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
//	    		capabilities.setCapability("requireWindowFocus", true);
//	    		File ie_temp=new File(CommonConst.WEBDRIVER_PATH+File.separator+"IEDriver.tmp");
//	    		InternetExplorerDriverService.Builder 
//	    		ies=new InternetExplorerDriverService.Builder();
//	    		ies.withExtractPath(ie_temp);
//	    		InternetExplorerDriverService service=ies.build();
//	    		ret = new InternetExplorerDriver(service,capabilities);
	    		
	    		ret = new InternetExplorerDriver(capabilities);
	    		break;
    		default:
    			break;
    	}
    	return ret;
        
    }
}
