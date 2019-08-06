/**
 *
 */
package io.koreada.crawler;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private ChromeOptions options = null;
    
    // Constructor
    public Crawler() throws FileNotFoundException, IOException {
    	//System Property SetUp
        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.CHROME_DRIVER_PATH);
        
        //Driver SetUp
        options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", true);
//        options.addArguments("headless");
        options.addExtensions(new File(CommonConst.TOUCH_EN_PC_PATH));
        capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
         
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
    
    @SuppressWarnings("deprecation")
	private ArrayList<?> operate(){
    	ArrayList ret = null;
    	try {
			driver = new ChromeDriver(capabilities);
			String inputLine = null;
	        StringBuffer response = null;
	        
	        driver.get(mUrl);
            Thread.sleep(3000);

            WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
            WebElement cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
            
            accountElement.click();
            accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));
            passElement.click();
            passElement.sendKeys("0409");
            bizNoElement.click();
	        bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
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
		} catch (Exception e) {
			e.printStackTrace();
			Debug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
		} finally {
			driver.close();
			driver.quit();
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
