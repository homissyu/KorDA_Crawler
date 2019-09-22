/**
 *
 */
package io.koreada.crawler;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.koreada.parser.HashCodeList;
import io.koreada.parser.IBK;
import io.koreada.util.CommonConst;
import io.koreada.util.CommonUtil;
import io.koreada.util.Debug;
import io.koreada.util.Install;

/**
 * @author user
 *
 */
public class CrawlerForKeyboardSecurity {

	private static String SUBSYSTEM = "Crawler4IBK";

    protected static Install mInstall = null;
    protected static Debug mDebug = null;

    public boolean mShutdown = false;
    private int iInterval = 0;
    private String mUrl = null;
    private String mParam = null;
    
	private IBK ibkParser = new IBK();
	private HashCodeList mOldHashCodeList = null;
	private HashCodeList mNewHashCodeList = null;
	
	private ObjectMapper mapper = new ObjectMapper();
	private String aFileName = CommonConst.ACCOUNT_INFO_NAME + CommonConst.CURRENT_DIR + CommonConst.JSON_EXTENSION;
	private JsonGenerator jg = null;
	
	//WebDriver
    private WebDriver driver;
    private DesiredCapabilities capabilities = null;
    private ChromeOptions mChromeOptions = null;
    private FirefoxOptions mFirefoxOptions = null;
    private SafariOptions mSafariOptions = null;
    
    private String mWebDriverName = null;
    private String mWebDriverID = null;
    
    private int iType = 0;
    
    // Constructor
    public CrawlerForKeyboardSecurity() throws FileNotFoundException, IOException {
    	driver = getDriver();
    }
    
    private WebDriver getDriver() {
    	iType = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_WEBDRIVER));
    	mWebDriverID = CommonConst.WEBDRIVER_ID_ARR[iType];
    	mWebDriverName = CommonConst.WEBDRIVER_STR_ARR[iType];
    	System.setProperty(mWebDriverID, mInstall.getLibDir()+File.separator+CommonConst.WEBDRIVER_PATH+File.separator+mWebDriverName);
    	capabilities = new DesiredCapabilities();
    	WebDriver ret = null;
    	
    	switch(iType) {
	    	case 0:
	    		mChromeOptions = new ChromeOptions();
	        	mChromeOptions.setCapability("ignoreProtectedModeSettings", true);
//	            mChromeOptions.addArguments("headless");
	        	mChromeOptions.addExtensions(new File(mInstall.getLibDir()+File.separator+CommonConst.TOUCH_EN_CHROME_PATH));
	        	capabilities.setCapability(ChromeOptions.CAPABILITY, mChromeOptions);
	        	capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
	        	ret = new ChromeDriver(capabilities);
	            break;
	    	case 1:
	    		mFirefoxOptions = new FirefoxOptions();
	    		mFirefoxOptions.setCapability("ignoreProtectedModeSettings", true);
	    		FirefoxProfile profile = new FirefoxProfile();
	    		
//	    		FirefoxBinary firefoxBinary = new FirefoxBinary();
//	    	    firefoxBinary.addCommandLineOptions("--headless");
//	    	    mFirefoxOptions.setBinary(firefoxBinary);

	    		profile.setPreference("devtools.toolbox.selectedTool", "netmonitor");
	    		profile.setPreference("devtools.toolbox.footer.height", 0);
	    		profile.setPreference("devtools.devedition.promo.enabled", false);
	    		profile.setPreference("devtools.devedition.promo.shown", false);
	    		profile.addExtension(new File(mInstall.getLibDir()+File.separator+CommonConst.TOUCH_EN_FIREFOX_PATH));
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
	    		break;
	    	case 5:
	    		break;
    		default:
    			break;
    	}
    	return ret;
        
    }

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_SEL_IP);
    	mUrl = URLDecoder.decode(mUrl);
    	
    	mParam = mInstall.getProperty(Install.SMART_BRIDGE_PARAM);
    	mParam = URLDecoder.decode(mParam);
    	
    	ScheduleExecuteTask job = new ScheduleExecuteTask();
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(job, 0, iInterval);
	    
//	    driver = getDriver();
	}

    // Initialize Method(Static Field Initial)
    private static void initialize(String[] args) {
        if (mInstall == null) {
            try {
                mInstall = new Install(args);
                mDebug = new Debug(mInstall);
            } catch (Exception e) {
            	mInstall = null;
            	mDebug = null;
            	Debug.traceError(SUBSYSTEM, e, "Exception occured : "+e.getLocalizedMessage());
            	System.exit(1);
            }
        }
    }

    // Shutdown Method, Connection disconnect, System.exit
    public void shutdown() {
    	Debug.trace(SUBSYSTEM, 1, "Shutting down....");
    	mShutdown = true;
        Debug.closeErrLog();
        driver.close();
        System.exit(0);
    }
    
    private ArrayList<?> operate(){
    	ArrayList ret = null;
    	try {
    		String inputLine = null;
	       StringBuffer response = null;
	       
	       driver.get(mUrl);
	       Thread.sleep(5000);
	       JavascriptExecutor jse = (JavascriptExecutor)driver;
	       
	       WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
	       WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
	       WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
	       WebElement cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
            
	       new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(accountElement));
	       accountElement.click();
	       accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));

	       new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(passElement));
	       passElement.click();
            
          WebElement passMap = driver.findElement(By.xpath(".//*[@id='acnt_pwd_layoutSingle']"));
          new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(passMap));
            	
          Actions builder = new Actions(driver);
//            	System.out.println(passElement.getLocation().getX());
            	int [] p0 = {passElement.getLocation().getX()+190, passElement.getLocation().getY()+70};//0
            	int [] p1 = {-40, 120};//4
            	int [] p2 = {40, -120};//0
            	int [] p3 = {-120, 0};//9
            	int [] p4 = {-40, -30};//End

            	builder.moveByOffset(p0[0], p0[1]).click().build().perform();
            	Thread.sleep(100);
            	builder.moveByOffset(p1[0], p1[1]).click().build().perform();
            	Thread.sleep(100);
            	builder.moveByOffset(p2[0], p2[1]).click().build().perform();
            	Thread.sleep(100);
            	builder.moveByOffset(p3[0], p3[1]).click().build().perform();
            	Thread.sleep(100);
            	builder.moveByOffset(p4[0], p4[1]).click().build().perform();
            	
            	Thread.sleep(1000);
            	
            	new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(bizNoElement));
              bizNoElement.click();

              passMap = driver.findElement(By.xpath(".//*[@id='rnno_layoutSingle']"));
              new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(passMap));
              
              Actions builder2 = new Actions(driver);
              	
//            	int [] p5 = {bizNoElement.getLocation().getX()+65, bizNoElement.getLocation().getY()+70};//8
            	int [] p5 = {25, 100};//8
            	int [] p6 = {125, 0};//1
            	int [] p7 = {-40, -40};//0
            	int [] p8 = {40, 40};//1
            	int [] p9 = {-40, 80};//4
            	int [] p10 = {-80, 0};//6
            	int [] p11 = {80, -120};//0
            	int [] p12 = {80, 20};//End

            	builder2.moveToElement(bizNoElement);
            	builder2.moveByOffset(p5[0], p5[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p6[0], p6[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p7[0], p7[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p8[0], p8[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p9[0], p9[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p10[0], p10[1]).click().build().perform();
            	Thread.sleep(100);
            	builder2.moveByOffset(p11[0], p11[1]).click().build().perform();
            	Thread.sleep(100);
//            	builder2.moveByOffset(p12[0], p12[1]).click().build().perform();
//            	Thread.sleep(100000);
//	        bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
            	new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(cateElement));
            	cateElement.click();
            	cateElement.sendKeys("1");
	        
	        jse.executeScript("setCount2(30)");

	        //	        WebElement noDataElement = driver.findElement(By.xpath(".//*[@class='no_data']"));
	        
            WebElement gridArea = driver.findElement(By.xpath("//div[@id='grid_area']"));
            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(gridArea));
          
            inputLine = ((WebElement)driver.findElement(By.xpath("//*[@id=\"ibk_grid_main\"]/script[2]"))).getAttribute("innerText");
			
            response = new StringBuffer();
            if(inputLine.contains("RgUtil.setInsertRecords('grid_area', '")) {
            	response.append(
        				(inputLine.split("grid_area")[3]).split("'")[2]
        		);
        	}else if(inputLine.contains("\"result\":\"error\"")) throw new Exception("Exception Occured! : "+inputLine);

//	        ret = ibkParser.parse(response.toString());
	        mNewHashCodeList = ibkParser.getHashCodeList();
	        System.out.println("mNewHashCode:"+mNewHashCodeList);
	        if(mOldHashCodeList.equals(mNewHashCodeList)) {
	        	ret.removeAll(ret);
	        	ret.add("No Change");
	        }
	        System.out.println("mOldHashCode:"+mOldHashCodeList);
	        mOldHashCodeList.addAll(mNewHashCodeList);
			
		} catch (UnhandledAlertException e) {
		      Alert alert = driver.switchTo().alert();
//		      ret.add(alert.getText());
		      alert.dismiss();
		      Debug.traceError(SUBSYSTEM, e,e.getLocalizedMessage()+" Not Available Yet !!");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.traceError(SUBSYSTEM, e,e.getLocalizedMessage()+" Not Available Yet !!");
		} finally {
			driver.close();
			if(driver != null)driver.quit();
		}
		return ret;
	}
    
   	// Main Method
	public static void main(String[] args) {
		CrawlerForKeyboardSecurity.initialize(args);
		CrawlerForKeyboardSecurity wc = null;
		try {
			wc = new CrawlerForKeyboardSecurity();
			wc.start(args);
		} catch (Exception e) {
			e.printStackTrace();
			wc.driver.close();
			wc.driver.quit();
		}finally {
//			System.out.println("finally");
//			wc.driver.close();
		}
    }

	class ScheduleExecuteTask extends TimerTask {
		private ArrayList<?> obj = null;
		
    	public void run(){
			try {	
		    	if(!mShutdown){
		    		obj = operate();
		    		System.out.println(obj);
		    		if(Install.RESULT_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_TYPE))
		    				|| !obj.contains("No Change")) {
		    			if(Install.RESULT_LOG_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_LOG_TYPE))) {
				    		mapper.writeValue(jg, obj);
				    		jg.writeRaw(System.lineSeparator());
				    	}else {
				    		mapper.writeValue(new File(CommonConst.CURRENT_DIR + File.separator + CommonConst.LOGS_DIR + File.separator + CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+ CommonConst.CURRENT_DIR + aFileName), obj);
				    	}
		    		}
		    	}else{
		    		Debug.closeErrLog();
		    		jg.close();
		    		System.exit(0);
		    	}
			}catch(Exception ex) {
				ex.printStackTrace();
			} 
	    }		
	}

}
