/**
 *
 */
package io.koreada.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.koreada.parser.IBK;
import io.koreada.util.CommonConst;
import io.koreada.util.CommonUtil;
import io.koreada.util.Debug;
import io.koreada.util.Install;

/**
 * @author user
 *
 */
public class Crawler {

	private static String SUBSYSTEM = "Crawler4IBK";

    protected static Install mInstall = null;
    protected static Debug mDebug = null;

    public boolean mShutdown = false;
    private int iInterval = 0;
    private String mUrl = null;
    private String mParam = null;
    
	private IBK ibkParser = new IBK();
	private int mOldHashCode = 0;
	private int mNewHashCode = 0;
	
	private ObjectMapper mapper = new ObjectMapper();
	private String aFileName = CommonConst.ACCOUNT_INFO_NAME + CommonConst.CURRENT_DIR + CommonConst.JSON_EXTENSION;
	private JsonGenerator jg = null;
	
	//WebDriver
    private WebDriver driver;
    private DesiredCapabilities capabilities = null;
    private ChromeOptions mChromeOptions = null;
    private FirefoxOptions mFirefoxOptions = null;
    private SafariOptions mSafariOptions = null;
    private EdgeOptions mEdgeOptions = null;
    private InternetExplorerOptions mIEOptions = null;
    
    private String mWebDriverName = null;
    private String mWebDriverID = null;
    
    private int iType = 0;
    
    // Constructor
    public Crawler() throws FileNotFoundException, IOException {
    	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    	jg = mapper.getFactory().createGenerator(new FileOutputStream(new File(CommonConst.CURRENT_DIR + File.separator + CommonConst.LOGS_DIR + File.separator + aFileName)));
	}
    
    private WebDriver getDriver() {
    	iType = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_WEBDRIVER));
    	mWebDriverID = CommonConst.WEBDRIVER_ID_ARR[iType];
    	mWebDriverName = CommonConst.WEBDRIVER_STR_ARR[iType];
    	if(CommonConst.getOSName().equalsIgnoreCase("Windows")) mWebDriverName += ".exe";
    	System.setProperty(mWebDriverID, CommonConst.WEBDRIVER_PATH+File.separator+mWebDriverName);
    	capabilities = new DesiredCapabilities();
    	WebDriver ret = null;
    	
    	switch(iType) {
	    	case 0:
	    		mChromeOptions = new ChromeOptions();
	        	mChromeOptions.setCapability("ignoreProtectedModeSettings", true);
//	            mChromeOptions.addArguments("headless");
	        	mChromeOptions.addExtensions(new File(CommonConst.TOUCH_EN_CHROME_PATH));
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
	    		profile.addExtension(new File(CommonConst.TOUCH_EN_FIREFOX_PATH));
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

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
    	mUrl = URLDecoder.decode(mUrl);
    	
    	mParam = mInstall.getProperty(Install.SMART_BRIDGE_PARAM);
    	mParam = URLDecoder.decode(mParam);
    	
    	ScheduleExecuteTask job = new ScheduleExecuteTask();
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(job, 0, iInterval);
	    
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
            	mDebug.traceError(SUBSYSTEM, e, "Exception occured : "+e.getLocalizedMessage());
            	System.exit(1);
            }
        }
    }

    // Shutdown Method, Connection disconnect, System.exit
    public void shutdown() {
    	mDebug.trace(SUBSYSTEM, 1, "Shutting down....");
    	mShutdown = true;
    	mDebug.closeErrLog();
        driver.close();
        System.exit(0);
    }
    
    private ArrayList<?> operate(){
    	ArrayList ret = null;
    	try {
    		driver = getDriver();
		
			String inputLine = null;
	        StringBuffer response = null;
	        
	        driver.get(mUrl);
            Thread.sleep(1000);

            WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
            WebElement cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
            
//            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(accountElement));
            accountElement.click();
            accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));
            passElement.click();
            passElement.sendKeys("0409");
            bizNoElement.click();
            bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
//	        Thread.sleep(1000);
	        cateElement.click();
	        cateElement.sendKeys("1");
	        
	        JavascriptExecutor jse = (JavascriptExecutor)driver;
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

	        ret = ibkParser.parse(response.toString());
	        mNewHashCode = ibkParser.getHashCode();
	        if(mOldHashCode == mNewHashCode) {
	        	ret.removeAll(ret);
	        	ret.add("No Change");
	        }
	        mOldHashCode = mNewHashCode;
			
		} catch (UnhandledAlertException e) {
		    Alert alert = driver.switchTo().alert();
//		      ret.add(alert.getText());
		    alert.dismiss();
		    mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
		} catch (Exception e) {
			e.printStackTrace();
			mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
		} finally {
			driver.close();
			if(iType==0 || driver != null)driver.quit();
		}
		return ret;
	}
    
   	// Main Method
	public static void main(String[] args) {
		Crawler.initialize(args);
		Crawler wc = null;
		try {
			wc = new Crawler();
			wc.start(args);
		} catch (Exception e) {
			e.printStackTrace();
			mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage());
			wc.driver.close();
			wc.driver.quit();
		}finally {
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
		    		jg.close();
		    		shutdown();
		    	}
			}catch(Exception ex) {
				ex.printStackTrace();
				mDebug.trace(SUBSYSTEM, 0,ex.getLocalizedMessage());
			} 
	    }		
	}

}
