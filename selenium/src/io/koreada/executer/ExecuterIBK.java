package io.koreada.executer;

import java.net.URLDecoder;
import java.util.ArrayList;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.koreada.parser.AccountInfo;
import io.koreada.parser.HashCodeList;
import io.koreada.parser.IBK;
import io.koreada.supportfactory.WebdriverFactory;
import io.koreada.util.Debug;
import io.koreada.util.Install;

public class ExecuterIBK extends Executer {
	private static String SUBSYSTEM = "Crawler4IBK";
	
	private Debug mDebug = null;
	private Install mInstall = null;
	
    private String mUrl = null;
    private String mParam = null;
    
	private IBK ibkParser = new IBK();
	private HashCodeList mOldHashCodeList = new HashCodeList();
	private HashCodeList mNewHashCodeList = new HashCodeList();
	
	private WebdriverFactory wf = null;
	private WebDriver driver = null;
	
	@SuppressWarnings("deprecation")
	public ExecuterIBK(Debug aDebug, Install aInstall, HashCodeList aHashCodeList) {
		mDebug = aDebug;
		mInstall = aInstall;
		
		mUrl = mInstall.getProperty(Install.SMART_BRIDGE_SEL_IP);
    	mUrl = URLDecoder.decode(mUrl);
    	
    	mParam = mInstall.getProperty(Install.SMART_BRIDGE_PARAM);
    	mParam = URLDecoder.decode(mParam);
    	
    	wf = new WebdriverFactory(mDebug, mInstall);
    	
    	if(aHashCodeList != null)mOldHashCodeList = aHashCodeList;
	}
	
	public HashCodeList getHashCodeList() {
		return this.ibkParser.getHashCodeList();
	}
	
	public void closeDriver() {
		this.driver.close();
		if(this.driver != null) this.driver.quit();
	}
	
	public ArrayList<?> operate(int iMode){
		this.driver = wf.getDriver();
    	ArrayList<?> ret = new ArrayList();
    	try {
    		String inputLine = null;
	        StringBuffer response = null;
	        
	        driver.get(mUrl);
            Thread.sleep(100);

            WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
            WebElement cateElement = null;
            switch(iMode) {
	            case 0:
	            	cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_01']"));
	            	break;
	            case 1:
	            	cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
	            	break;
	            case 2:
	            	cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_03']"));
	            	break;
	            default:
	            	cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_01']"));
	            	break;
            }
            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(accountElement));
            accountElement.click();
            accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));
            Thread.sleep(100);
            passElement.click();
            passElement.sendKeys(this.mInstall.getProperty(Install.SMART_BRIDGE_BANK_PASS));
            Thread.sleep(100);
            bizNoElement.click();
            bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
            Thread.sleep(100);
	        cateElement.click();
	        cateElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_CRAWLER_TYPE));
	        Thread.sleep(100);

	        JavascriptExecutor jse = (JavascriptExecutor)driver;
	        jse.executeScript("setCount2(30)");

            WebElement gridArea = driver.findElement(By.xpath("//div[@id='grid_area']"));
            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(gridArea));
          
            inputLine = ((WebElement)driver.findElement(By.xpath("//*[@id=\"ibk_grid_main\"]/script[2]"))).getAttribute("innerText");
			
            response = new StringBuffer();
            if(inputLine.contains("RgUtil.setInsertRecords('grid_area', '")) {
            	response.append(
        				(inputLine.split("grid_area")[3]).split("'")[2]
        		);
        	}else if(inputLine.contains("\"result\":\"error\"")) throw new Exception("Exception Occured! : "+inputLine);
//            System.out.println("response.toString():"+response.toString());
	        ret = ibkParser.parse(response.toString());
	        System.out.println("ret:"+ret);
//	        System.out.println("ibkParser.getHashCodeList():"+ ibkParser.getHashCodeList());

	        mNewHashCodeList.clear();
	        mNewHashCodeList.addAll(ibkParser.getHashCodeList());
	        
//	        System.out.println("mNewHashCode:"+mNewHashCodeList.getHashCodeList());
//	        System.out.println("mOldHashCode:"+mOldHashCodeList.getHashCodeList());
	        
	        if(mOldHashCodeList.equals(mNewHashCodeList)) {
	        	ret.clear();
//	        	System.out.println("There isn't new one");
	        }else {
	        	AccountInfo aInfo = null;
	        	ArrayList newRet = new ArrayList();
	        	int iBreak = 0;
	        	for(int i=0;i<ret.size();i++){
	        		aInfo = (AccountInfo)ret.get(i);
//	        		System.out.println("aInfo:"+aInfo.toString());
	        		iBreak = i;
	        		if(mOldHashCodeList.contains(aInfo.getHashCode())) break;
//	        		System.out.println(iBreak);
	        	}
	        	for(int i=0;i<iBreak;i++) {
	        		newRet.add(i, (AccountInfo)ret.get(i));
	        	}
	        	ret.clear();
	        	ret.addAll(newRet);
	        }
	        
//	        System.out.println(ret);
	        mOldHashCodeList.clear();
	        mOldHashCodeList.addAll(mNewHashCodeList);
		} catch (UnhandledAlertException e) {
		    Alert alert = driver.switchTo().alert();
		    alert.dismiss();
		    Debug.traceError(SUBSYSTEM, e,e.getLocalizedMessage()+" Not Available Yet !!");
		} catch (Exception e) {
			Debug.traceError(SUBSYSTEM, e,e.getLocalizedMessage()+" Not Available Yet !!");
			e.printStackTrace();
		} finally {
			closeDriver();
		}
		return ret;
	}
}
