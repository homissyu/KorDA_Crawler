package io.koreada.executer;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.koreada.parser.HashCodeList;
import io.koreada.parser.IBK;
import io.koreada.supportfactory.VKeyboardFactory;
import io.koreada.supportfactory.WebdriverFactory;
import io.koreada.util.CryptoUtils;
import io.koreada.util.Debug;
import io.koreada.util.Install;

public class ExecuterNH extends Executer {
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
	public ExecuterNH(Debug aDebug, Install aInstall) {
		mDebug = aDebug;
		mInstall = aInstall;
		
		mUrl = mInstall.getProperty(Install.SMART_BRIDGE_SEL_IP);
    	mUrl = URLDecoder.decode(mUrl);
    	
    	mParam = mInstall.getProperty(Install.SMART_BRIDGE_PARAM);
    	mParam = URLDecoder.decode(mParam);
    	
    	wf = new WebdriverFactory(mDebug, mInstall);
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
	        Actions builder = new Actions(driver);
            Thread.sleep(1000);
            
            JavascriptExecutor jse = (JavascriptExecutor)driver;
	        jse.executeScript("icsi_siGnbPageMove()");
	        WebElement subMenu = driver.findElement(By.xpath(".//*[@id='subMenu0']"));
	        new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(subMenu));
	        jse.executeScript("icsi_popUrlLink('ICSI0010I', 'P01')");
	        Thread.sleep(1000);
	        
//	        WebElement freeAccElement = driver.findElement(By.xpath(".//*[@id='a1567136977665']"));
	        WebElement accountElement = driver.findElement(By.xpath(".//*[@id='acno']"));
            WebElement passElement = driver.findElement(By.xpath(".//*[@id='io_pw_30']"));
            WebElement compElement = driver.findElement(By.xpath(".//*[@id='io_coppsn_dsc']"));
            WebElement bizNoElement = driver.findElement(By.xpath(".//*[@id='rlno']"));
            WebElement newTrxElement = driver.findElement(By.xpath(".//*[@name='schRdo2']"));
            WebElement depositElement = driver.findElement(By.xpath(".//*[@name='schRdo3']"));
            
            jse.executeScript("rdoChange(1)");
            
            accountElement.click();
            Thread.sleep(1000);
            
            WebElement tempEle = driver.findElement(By.xpath(".//*[@id='Tk_acno_layoutSingle']"));
            WebElement VirtualKeyboardImage = tempEle.findElement(By.id("imgSingle"));
            String vKHashCode = CryptoUtils.generateSHA1(VirtualKeyboardImage.getScreenshotAs(OutputType.BASE64));
//            System.out.println(vKHashCode);
            ObjectMapper mapper = new ObjectMapper();
            VKeyboardFactory [] vk = mapper.readValue(new File(mInstall.getLibDir()+File.separator + "vkeyboard" + File.separator + "NH" + File.separator + "vk.json"), VKeyboardFactory[].class);
            ArrayList<VKeyboardFactory> vkList = new ArrayList<VKeyboardFactory>(Arrays.asList(vk));
            VKeyboardFactory vkf = null;
            for(int i=0;i<vkList.size();i++) {
            	if((vkList.get(i).hashcode).equals(vKHashCode)) {
            		vkf = (VKeyboardFactory)vkList.get(i);
            		break;
            	}
            }
            
//            System.out.println(vkf.hashcode);
            
            int [] p = {accountElement.getLocation().getX(), accountElement.getLocation().getY()};//Initial
        	
        	builder.moveByOffset(vkf.k_0[0], vkf.k_0[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_1[0]-vkf.k_0[0], vkf.k_1[1]-vkf.k_0[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_2[0]-vkf.k_1[1], vkf.k_2[1]-vkf.k_1[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_3[0]-vkf.k_2[1], vkf.k_3[1]-vkf.k_2[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_4[0]-vkf.k_3[1], vkf.k_4[1]-vkf.k_3[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_5[0]-vkf.k_4[1], vkf.k_5[1]-vkf.k_4[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_6[0]-vkf.k_5[1], vkf.k_6[1]-vkf.k_5[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_7[0]-vkf.k_6[1], vkf.k_7[1]-vkf.k_6[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_8[0]-vkf.k_7[1], vkf.k_8[1]-vkf.k_7[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_9[0]-vkf.k_8[0], vkf.k_9[1]-vkf.k_8[1]).click().build().perform();
        	Thread.sleep(100);
        	builder.moveByOffset(vkf.k_enter[0]-vkf.k_9[1], vkf.k_enter[1]-vkf.k_9[1]).click().build().perform();
        	Thread.sleep(100);
        	
            passElement.click();
//            passElement.sendKeys("0409");
            Thread.sleep(100);
            
            compElement.click();
            Thread.sleep(100);
            
            bizNoElement.click();
//            bizNoElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_BIZ_NO));
            Thread.sleep(100);
            
            newTrxElement.sendKeys("2");
            Thread.sleep(100);
            
            depositElement.sendKeys("2");
	        Thread.sleep(100);
	        
	        WebElement btnSpanElement = driver.findElement(By.xpath(".//*[@class='btn1']"));
	        WebElement btnElement = btnSpanElement.findElement(By.tagName("A"));
	        
	        btnElement.click();
	        
//	        jse.executeScript("setCount2(30)");

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

//	        System.out.println("ret:"+ret);
//	        System.out.println("ibkParser.getHashCodeList():"+ ibkParser.getHashCodeList());

	        mNewHashCodeList.clear();
	        mNewHashCodeList.addAll(ibkParser.getHashCodeList());
	        
//	        System.out.println("mNewHashCode:"+mNewHashCodeList);
//	        System.out.println("mOldHashCode:"+mOldHashCodeList);
	        
	        if(mOldHashCodeList.equals(mNewHashCodeList)) {
	        	ret.clear();
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
