package io.koreada.executer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.koreada.parser.IBK;
import io.koreada.supportfactory.WebdriverFactory;
import io.koreada.util.CommonConst;
import io.koreada.util.CommonUtil;
import io.koreada.util.Debug;
import io.koreada.util.Install;

public class ExecuterNH extends Executer {
	private static String SUBSYSTEM = "Crawler4IBK";
	
	private Debug mDebug = null;
	private Install mInstall = null;
	
    private String mUrl = null;
    private String mParam = null;
    
	private IBK ibkParser = new IBK();
	private Set<Integer> mOldHashCodeList = new HashSet<Integer>();
	private Set<Integer> mNewHashCodeList = new HashSet<Integer>();
	
	private WebdriverFactory wf = null;
	private WebDriver driver = null;
	
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
            
//            new WebDriverWait(driver,30).until(ExpectedConditions.elementToBeClickable(freeAccElement));
//            freeAccElement.click();
            jse.executeScript("rdoChange(1)");
            
            accountElement.click();
            
            Thread.sleep(1000);
            
            WebElement tempEle = driver.findElement(By.xpath(".//*[@id='Tk_acno_layoutSingle']"));
            WebElement logo = tempEle.findElement(By.id("imgSingle"));
            System.out.println(logo.getScreenshotAs(OutputType.BASE64));
            
//            String sTemp = CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT);
//            
//            BufferedWriter out = new BufferedWriter(new FileWriter(new File("/Users/karlchoi/Desktop/NH/logo-image"+sTemp+".txt")));
//            out.write(logo.getScreenshotAs(OutputType.BASE64));
//            out.close();
//            
//            File imgFile = logo.getScreenshotAs(OutputType.FILE);
//            FileHandler.copy(imgFile, new File("/Users/karlchoi/Desktop/NH/logo-image"+sTemp+".png"));
//            
//            
//            
//            
//            
//            URL imageURL = new URL(logoSRC);
//	        HttpsURLConnection mHttpsConn = (HttpsURLConnection) imageURL.openConnection();
//	    	mHttpsConn.setRequestMethod("GET");
//			mHttpsConn.setDoInput(true);
//			mHttpsConn.setDoOutput(true);
//System.out.println(mHttpsConn.getURL());            
//			DataOutputStream dos = new DataOutputStream(mHttpsConn.getOutputStream());
//			dos.flush();
//			dos.close();
//System.out.println(mHttpsConn.getResponseCode());			
//			BufferedImage saveImage = ImageIO.read(mHttpsConn.getInputStream());
//System.out.println(mHttpsConn.getInputStream());
//System.out.println(mHttpsConn);
//System.out.println(saveImage);
//            ImageIO.write(saveImage, "png", new File("/Users/karlchoi/Desktop/NH/logo-image.png"));
            
//	        accountElement.sendKeys(mInstall.getProperty(Install.SMART_BRIDGE_ACC_NO));
            
//        	System.out.println(passElement.getLocation().getX());
            int [] p = {accountElement.getLocation().getX(), accountElement.getLocation().getY()};//Initial
        	
//          int [] p0 = {305, 275};//0
//        	int [] p1 = {340, 275};//1
//        	int [] p2 = {340, 345};//2
//        	int [] p3 = {340, 385};//3
//        	int [] p4 = {300, 385};//4
//        	int [] p5 = {270, 385};//5
//        	int [] p6 = {230, 385};//6
//        	int [] p7 = {230, 345};//7
//        	int [] p8 = {230, 275};//8
//        	int [] p9 = {270, 275};//9
//        	int [] pEnd = {380, 300};//End
        	
        	int [] p0 = {305, 275};//0
        	int [] p1 = {35, 0};//1
        	int [] p2 = {0, 70};//2
        	int [] p3 = {0, 40};//3
        	int [] p4 = {-40, 0};//4
        	int [] p5 = {-40, 0};//5
        	int [] p6 = {-40, 0};//6
        	int [] p7 = {230, 345};//7
        	int [] p8 = {230, 275};//8
        	int [] p9 = {270, 275};//9
        	int [] pEnd = {380, 300};//End

//        	System.out.println(p[0]+":"+p[1]);
        	
        	builder.moveByOffset(p0[0], p0[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p1[0], p1[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p2[0], p2[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p3[0], p3[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p4[0], p4[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p5[0], p5[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p6[0], p6[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p7[0], p7[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p8[0], p8[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(p9[0], p9[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	builder.moveByOffset(pEnd[0], pEnd[1]).click().build().perform();
        	Thread.sleep(100);
        	System.out.println((accountElement.getAttribute("value")).length());
        	
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
