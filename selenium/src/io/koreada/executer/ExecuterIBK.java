package io.koreada.executer;

import java.io.File;
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
import io.koreada.util.CommonConst;
import io.koreada.util.Debug;
import io.koreada.util.FileHandler;
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
	public ExecuterIBK(Debug aDebug, Install aInstall) {
		mDebug = aDebug;
		mInstall = aInstall;
		
		mUrl = mInstall.getProperty(Install.SMART_BRIDGE_SEL_IP);
    	mUrl = URLDecoder.decode(mUrl);
    	
    	mParam = mInstall.getProperty(Install.SMART_BRIDGE_PARAM);
    	mParam = URLDecoder.decode(mParam);
    	
    	wf = new WebdriverFactory(mDebug, mInstall);
    	File aFile = new File(mInstall.getRootDir() + File.separator + CommonConst.BACKUP_FILE_NAME);
//    	System.out.println("aFile.exists():"+aFile.exists());
    	if(aFile.exists()) {
    		mOldHashCodeList = readHashCodeListFile();
//    		System.out.println((this.mOldHashCodeList.getHashCodeList()).toString());
    	}
	}
	
	private void writeHashCodeListFile(HashCodeList hashCodeList) {
		FileHandler.writeSerFile(hashCodeList, mInstall.getRootDir(), CommonConst.BACKUP_FILE_NAME);
	}
	
	private HashCodeList readHashCodeListFile() {
		return (HashCodeList)FileHandler.readSerFile(mInstall.getRootDir() + File.separator + CommonConst.BACKUP_FILE_NAME);
	}
	
	public void closeDriver() {
		this.driver.close();
		if(this.driver != null) this.driver.quit();
	}
	
	public ArrayList<?> operate(){
		this.driver = wf.getDriver();
    	ArrayList<?> ret = new ArrayList();
    	try {
    		String inputLine = null;
	        StringBuffer response = null;
	        
	        driver.get(mUrl);
            Thread.sleep(1000);

            WebElement accountElement = driver.findElement(By.xpath(".//*[@id='in_cus_acn']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='acnt_pwd']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rnno']"));
            WebElement cateElement = driver.findElement(By.xpath(".//*[@id='rdo_inq_dcd_02']"));
            
            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(accountElement));
            accountElement.click();
            accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));
            Thread.sleep(100);
            passElement.click();
            passElement.sendKeys("0409");
            Thread.sleep(100);
            bizNoElement.click();
            bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
            Thread.sleep(100);
	        cateElement.click();
	        cateElement.sendKeys("1");
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

	        ret = ibkParser.parse(response.toString());
	        writeHashCodeListFile(ibkParser.getHashCodeList());
//	        System.out.println("ret:"+ret);
//	        System.out.println("ibkParser.getHashCodeList():"+ ibkParser.getHashCodeList());

	        mNewHashCodeList.clear();
	        mNewHashCodeList.addAll(ibkParser.getHashCodeList());
	        
//	        System.out.println("mNewHashCode:"+mNewHashCodeList.getHashCodeList());
//	        System.out.println("mOldHashCode:"+mOldHashCodeList.getHashCodeList());
	        
	        if(mOldHashCodeList.equals(mNewHashCodeList)) {
	        	ret.clear();
	        }else {
	        	AccountInfo aInfo = null;
	        	ArrayList newRet = new ArrayList();
	        	int iBreak = 0;
	        	for(int i=0;i<ret.size();i++){
	        		aInfo = (AccountInfo)ret.get(i);
	        		System.out.println("aInfo:"+aInfo.toString());
	        		if(mOldHashCodeList.contains(aInfo.getHashCode())) iBreak = i;
	        	}
	        	for(int i=0;i<=iBreak;i++) {
	        		newRet.add(i, ret.get(i));
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
		    mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
		} catch (Exception e) {
			mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
			e.printStackTrace();
		} finally {
			closeDriver();
		}
		return ret;
	}
}
