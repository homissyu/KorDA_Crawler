package io.koreada.selenium;

import java.io.File;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.koreada.util.CommonConst;

public class LoginKiupIBK {
	public static void main(String[] args) {
		 
		LoginKiupIBK dl = new LoginKiupIBK();
        dl.crawl();
    }
 
    
    //WebDriver
    private WebDriver driver;
    
    private WebElement momElement;
    
    //크롤링 할 URL
    private String base_url;
    
    @SuppressWarnings("deprecation")
	public LoginKiupIBK() {
    	
        //System Property SetUp
        System.setProperty(CommonConst.CHROME_DRIVER_ID, CommonConst.CHROME_DRIVER_PATH);
        
                
        //Driver SetUp
         ChromeOptions options = new ChromeOptions();
         options.setCapability("ignoreProtectedModeSettings", true);
//         options.addArguments("headless");
         
         options.addExtensions(new File("/Users/karlchoi/Downloads/TouchEn-PC보안-확장_v1.0.1.15.crx"));
         DesiredCapabilities capabilities = new DesiredCapabilities();
         capabilities.setCapability(ChromeOptions.CAPABILITY, options);
         driver = new ChromeDriver(capabilities);
         
//         driver = new ChromeDriver(options);
        
        base_url = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp?width=900&height=680&scroll=yes";
        
        
        
    }
 
    public void crawl() {
 
        try {
            //get page (= 브라우저에서 url을 주소창에 넣은 후 request 한 것과 같다)
            driver.get(base_url);
            Thread.sleep(3000);
//            momElement = driver.findElement(By.className("list"));
//            WebElement accountElement = momElement.findElement(By.id("in_cus_acn"));
//            WebElement passElement = momElement.findElement(By.id("acnt_pwd"));
//            WebElement bizNoElement = momElement.findElement(By.id("rnno"));
            
            momElement = driver.findElement(By.xpath(".//*[@class='list']"));
//            WebElement accountElement = momElement.findElement(By.xpath(".//*[@id='in_cus_acn']"));
//            WebElement passElement = momElement.findElement(By.xpath(".//*[@id='acnt_pwd']"));
//            WebElement bizNoElement = momElement.findElement(By.xpath(".//*[@id='rnno']"));
//            Thread.sleep(3000);
//          accountElement.sendKeys("05414414001019");
//          passElement.sendKeys("0409");
//          bizNoElement.sendKeys("8101460");
            
            JavascriptExecutor jse = (JavascriptExecutor)driver;
//            jse.executeScript("arguments[0].value='05414414001019';", accountElement);
//            jse.executeScript("arguments[0].value='0409';", passElement);
//            jse.executeScript("arguments[0].value='8101460';", bizNoElement);
            
            
            jse.executeScript("document.getElementById('in_cus_acn').click;");
            jse.executeScript("document.getElementById('in_cus_acn').focus();");
            Thread.sleep(1000);
            jse.executeScript("document.getElementById('in_cus_acn').value='05414414001019';");
            Thread.sleep(1000);
            
            jse.executeScript("document.getElementById('acnt_pwd').clck;");
            jse.executeScript("document.getElementById('acnt_pwd').focus();");
            Thread.sleep(1000);
            jse.executeScript("document.getElementById('acnt_pwd').value='0409';");
            Thread.sleep(1000);
            
            jse.executeScript("document.getElementById('rnno').click;");
            jse.executeScript("document.getElementById('rnno').focus();");
            Thread.sleep(1000);
            jse.executeScript("document.getElementById('rnno').value='8101460';");
            Thread.sleep(1000);         
            
//            momElement = momElement.findElement(By.className("tbl_txt3"));
//            ArrayList tempList = (ArrayList) momElement.findElements(By.tagName("A"));
//            momElement = driver.findElement(By.xpath(".//*[@id='btn_def']"));
            ArrayList<?> tempList = (ArrayList<?>) driver.findElements(By.xpath(".//*[@class='btn_lay_etc']"));
            WebElement schBtnElement = (WebElement) tempList.get(0);
            schBtnElement.click();
            
            Thread.sleep(3000);
//            momElement = momElement.findElement(By.id("grid_area"));
//            WebElement resultTable = momElement.findElement(By.tagName("TABLE"));
//            WebElement resultTbody = resultTable.findElement(By.tagName("TBODY"));
//            tempList = (ArrayList) resultTbody.findElements(By.tagName("TR"));
            momElement = driver.findElement(By.xpath("//*[@id='grid_area']"));
            WebElement resultTable = momElement.findElement(By.xpath("//TABLE"));
            WebElement resultTbody = resultTable.findElement(By.xpath("//TBODY"));
            tempList = (ArrayList<?>) resultTbody.findElements(By.xpath("//TR"));
            
            Thread.sleep(30000);
            
            
            ArrayList<AccountInfos> resultList = new ArrayList<AccountInfos>();
            
            for(int i=0;i<tempList.size();i++) {
            	AccountInfos aifs = new AccountInfos();
//            	aifs.setNo(
//            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.id("indicator")).getText())
//            			);
//            	aifs.setRegData(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_0")).getText()
//            			);
//            	aifs.setWithdraw(
//            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.id("col_1")).getText())
//            			);
//            	aifs.setDeposit(
//            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.id("col_2")).getText())
//            			);
//            	aifs.setBalance(
//            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.id("col_3")).getText())
//            			);
//            	aifs.setContents(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_4")).getText()
//            			);
//            	aifs.setSrcAccNo(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_5")).getText()
//            			);
//            	aifs.setSrcBank(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_6")).getText()
//            			);
//            	aifs.setCMSCode(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_7")).getText()
//            			);
//            	aifs.setTrType(
//            			((WebElement)tempList.get(i)).findElement(By.id("col_8")).getText()
//            			);
//            	aifs.setNonPay(
//            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.id("col_9")).getText())
//            			);
            	aifs.setNo(
            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='indicator']")).getText())
            			);
            	aifs.setRegData(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_0']")).getText()
            			);
            	aifs.setWithdraw(
            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_1']")).getText())
            			);
            	aifs.setDeposit(
            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_2']")).getText())
            			);
            	aifs.setBalance(
            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_3']")).getText())
            			);
            	aifs.setContents(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_4']")).getText()
            			);
            	aifs.setSrcAccNo(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_5']")).getText()
            			);
            	aifs.setSrcBank(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_6']")).getText()
            			);
            	aifs.setCMSCode(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_7']")).getText()
            			);
            	aifs.setTrType(
            			((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_8']")).getText()
            			);
            	aifs.setNonPay(
            			Integer.parseInt(((WebElement)tempList.get(i)).findElement(By.xpath(".//*[@id='col_9']")).getText())
            			);
            	
            	resultList.add(aifs);
            }

        } catch (Exception e) {
            
            e.printStackTrace();
        
        } finally {
 
            driver.close();
        }
        
        
    }
    
    class AccountInfos {
    	//순번, 거래일시, 출금금액, 입금금액, 잔액, 거래내용, 상대계좌번호, 상대은행, CMS코드, 거래구분, 미결제(수표/어음) 열로 이루어진 데이터입니다.
    	private int no = 0;
    	private String regData = null;
    	private int withdraw = 0;
    	private int deposit = 0;
    	private int balance = 0;
    	private String contents = null;
    	private String srcAccNo = null;
    	private String srcBank = null;
    	private String CMSCode = null;
    	private String trType = null;
    	private int nonPay = 0;
		
    	protected int getNo() {
			return no;
		}
		protected void setNo(int no) {
			this.no = no;
		}
		protected String getRegData() {
			return regData;
		}
		protected void setRegData(String regData) {
			this.regData = regData;
		}
		protected int getWithdraw() {
			return withdraw;
		}
		protected void setWithdraw(int withdraw) {
			this.withdraw = withdraw;
		}
		protected int getDeposit() {
			return deposit;
		}
		protected void setDeposit(int deposit) {
			this.deposit = deposit;
		}
		protected int getBalance() {
			return balance;
		}
		protected void setBalance(int balance) {
			this.balance = balance;
		}
		protected String getContents() {
			return contents;
		}
		protected void setContents(String contents) {
			this.contents = contents;
		}
		protected String getSrcAccNo() {
			return srcAccNo;
		}
		protected void setSrcAccNo(String srcAccNo) {
			this.srcAccNo = srcAccNo;
		}
		protected String getSrcBank() {
			return srcBank;
		}
		protected void setSrcBank(String srcBank) {
			this.srcBank = srcBank;
		}
		protected String getCMSCode() {
			return CMSCode;
		}
		protected void setCMSCode(String cMSCode) {
			CMSCode = cMSCode;
		}
		protected String getTrType() {
			return trType;
		}
		protected void setTrType(String trType) {
			this.trType = trType;
		}
		protected int getNonPay() {
			return nonPay;
		}
		protected void setNonPay(int nonPay) {
			this.nonPay = nonPay;
		}
    	
    	public String toString(){
    		StringBuffer ret = new StringBuffer();
    		ret.append(this.getNo());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getRegData());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getWithdraw());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getDeposit());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getBalance());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getCMSCode());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getSrcAccNo());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getSrcBank());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getCMSCode());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getTrType());
    		ret.append(CommonConst.COMMA);
    		ret.append(this.getNonPay());
    		return ret.toString();
    	}
    }
}
