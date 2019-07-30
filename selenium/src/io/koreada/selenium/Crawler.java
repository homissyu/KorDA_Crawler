/**
 *
 */
package io.koreada.selenium;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

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

    private static Crawler mCrawler = null;

    public boolean mShutdown = false;
    private int iInterval = 0;
    private String mUrl = null;
    
	private HttpsURLConnection mHttpsConn = null;

    // Constructor
    public Crawler() {
    }

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	
//    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
//    	System.out.println(mInstall.getProperty(Install.SMART_BRIDGE_IP));
    	mUrl = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp?inq_stdd=20190131&inq_endd=20190730&cus_acn=05414414001019&inq_dcd=1&otpt_trtp_dcd=2&sevr_prn_rqst_cnt=&ipinsideData=%3Dv1IeAHaZk%2Bcgn8fSUc1yWawb1qz9G8jKAEMTF%2BFyaSl7v8n4kx8K6JQt66UkckPEO%2F5sZA3llb6DMDx9GTqCRGxPHpaj2OclMpo6DficUrsgbqg0duuAwzJqrLFXECqsxUJYITEr8ARfs857wFBHHiof5gi27uWiSNBGxQaa%2BRCHvpUpzPHptyu%2BdV4x0OKFg920LCxy7xvy27XnRTD64SBgdXsOvXa4qFgxrpzl45HVffY%2FOOeuqW7IyKS8eKqLQVjvhADG%2B7qSp8rbFkRyRH%2FyXWPIXHMxHf%2FCfyFMAqDmd9DGmHXXuThp58c%2BNmrXrQgDxReSukQPpu%2FYCvmFYdT8AM%2FTO%2BLz%2FAofY1VQhUTF%2B4SG%2BmHvUIkNakVoi2Y2X7vk0T4zkSEpfG%2BYlgwaa4KQzgN4SDmEXy9t2VLNkVGCzW7zKpy%2BbTPlH9YCLHczyNuOYzDI%2BXtAUBxGGslvPXplDWT9xfQ%2FGjvUPEk9Nv7XbE14l9yb5yaAtXlQAC83z6OK4RRco5wXHj%2F7quglId%2FZc3BBBcTB2Je2FxvCeGs1bWxXTKbM81teNFunKwzJPmHOCN6U94t4%2F6bv&ipinsideNAT=xAk2HRloTCj7zhoosUkKabxvj4qETyEf885CeocX67NYB84224LAOvwM3StJTZw49y768CypiHq1uLkAGvXZHEnglLlAK4gFnTMcIiUeSfCITf6sAQ0DZscPOK7f1848Ma%2B9v4%2BjPZk6sIPJRZCNJIsKpBm6YkMWgk%2BFC2%2FcYQH%2BH7mdnSlyat9A2V9JgKfa&ipinsideCOMM=rPz9TJJd0pwzqbwkEMcVoBwR1b4FL24EJBCOobs%2FpJ1X8XO%2Fdbr%2BuUhYNjE8%2B6Amqdu8MLjy%2FrWCO4B6qOTvkSNQUSbYoeup5%2F7qPBG5FoHDkHgIgPNFeex5MDyruhwWPG%2B4b0Jr5mjLBJN1j4VchqMmDqRfhyXgrM2Rpr3g6zsJj0vv9ZxFJEQPRpiw24GJunWnolH2mFJXNCsNIB0krDNfXjD8bjiswxncDeOObPDuDlhXUigdR00xonBwwG02cqD89rmsW%2FnIL0ivgjiuKOler7oj9NxQmVEBZyestydvP9%2FHyuPyxmxW%2B4J%2BpiY365wUx44goV%2FAWuoqQN%2FnbIrkmn2Yj7VlAUdfpUfbl0Hdto24qwVnfrQI%2BrBQ0K6moA9JTJChKAaKLy8ptj%2BCnCqWM9gBJRABkCW5oCSx38%2FuLM5ScyzxKPOKCmcljqcF2GHncZ8V3PjAH1NKdtbw9w%3D%3D&W_SRVR_DATA_DLPR_EN=N&W_SRVR_DATA_DLPR=&pageIndex=1&rebound_yn=N&NEXT_PAGE_TRN_ID_2=&in_cus_acn=05414414001019&acnt_pwd=0000&rnno=0000000&rdo_inq_dcd=1&sel_otpt_trtp_dcd=2&inqy_sttg_ymd_yy=2019&inqy_sttg_ymd_mm=01&inqy_sttg_ymd_dd=31&inqy_sttg_ymdDateBoxType=period&inqy_sttg_ymdfromday=20140730%2F&inqy_sttg_ymdtoday=20190730%2F&inqy_sttg_ymd=2019-01-31&inqy_eymd_yy=2019&inqy_eymd_mm=07&inqy_eymd_dd=30&inqy_eymdDateBoxType=period&inqy_eymdfromday=20140730%2F&inqy_eymdtoday=20190730%2F&inqy_eymd=2019-07-30&_DUMMY_INPUT=&transkeyUuid=a260925e5ecae0a6659f3e4ae49a318b6fa0b0f89fb2d8235c0aa7368c9daac5&transkey_acnt_pwd=%249b%2Cfa%2C63%2C69%2C96%2Ca0%2Cd5%2C4b%2Ce8%2Cde%2C14%2C69%2Cf8%2C9f%2Cdc%2C5d%246a%2Cda%2Cfc%2Cb9%2Cc6%2Ca8%2C53%2Ca6%2C59%2Cf7%2C5e%2C59%2C7d%2Ca%2Cd9%2Cd0%2441%2Cfe%2C53%2C2d%2C88%2C13%2Ca2%2C8e%2C1%2Ce3%2C5b%2C7a%2Cc5%2C84%2Cf9%2C42%2478%2Cb2%2C37%2Cf0%2C11%2C2a%2C6d%2Cdd%2C80%2Cef%2C6d%2Cbc%2Cc4%2C8d%2C16%2C87%24da%2Ce4%2C5a%2C44%2C94%2Cb4%2C25%2Cf8%2Cdf%2Ca8%2C2b%2C22%2Ca3%2Ce8%2C92%2C4d&transkey_HM_acnt_pwd=c39d893ac3f01961b1b1efe222c186e683f265f50f012731f9a5f8417e845cd9&Tk_acnt_pwd_separator=e2e&transkey_rnno=%24b2%2C5a%2C87%2C46%2C52%2C97%2C94%2C9%2C77%2Cfb%2C42%2Cf5%2Ceb%2C18%2C6d%2C40%242f%2C35%2Cce%2C8c%2Cbe%2C1b%2Cab%2C43%2C63%2C24%2C82%2C35%2C39%2Cf9%2Cf3%2Ced%2423%2C99%2C33%2C42%2C9f%2C2a%2C5d%2Ca7%2C78%2Cf3%2C2c%2Ca6%2Ce5%2C9f%2Ce5%2C74%24f6%2Cc8%2C91%2Cfb%2Cd8%2C46%2Ca7%2C96%2C7b%2C80%2Cb3%2C47%2C9e%2C20%2Cac%2Cb2%2447%2C48%2C4f%2C9b%2C83%2C8c%2C5a%2C9e%2Cf9%2C41%2C20%2Cb%2C3f%2C8c%2Cfc%2C96%24ce%2C47%2Ced%2Cfd%2Ca1%2C72%2C31%2Cf6%2C16%2Ca4%2Ce3%2Ce5%2C21%2C94%2Cf3%2Cb1&transkey_HM_rnno=a9f5f673296264b34630192a537295fc9026a2dd9a09aa666557c2e1d052d2ce&Tk_rnno_separator=e2e&hid_key_data=773c378e16cf1d48c70b518cf290bbe30319ccd07370929cc776cb43085e39b9997a5bfbc693563bb49896514932f425115a10818259aab854ca8dcb55ac7d05d4ff92b78f04a878d080fe645c0f684475be366c727fddd5c4d4f0fe397634b2d9a760eb74294dc2c442026fd0afb3de7be1a2d4dc86e05971327c8919b27957abcb54a1e5de7c1e79e741c6aebec1235fa7722cc4070c6839ef86eeeef85aea855e4821269d3528079ad88753c6bb523466d67f0923bf279084589ad3d9485f9b747101ba7cc9943a83a29ac3d389e232079a0dfaa9fc58a86e0aa2981223e66bdd4dcdced71b457c0b45c9609da1617161e5c25d9487144c5bcc9fdea76a7c&hid_enc_data=&E2E_acnt_pwd=54e93be3d7c0e5a1da98c285f1b61ee66b459a833b84f1fd8f63fbf892667c337f1999204e252a188ff0103f7949dce93fc4caad5859d69a9a00313fbdab10da&E2E_rnno=c62ff2ecc824455d6717d764f0dc58126c073252f25c8c77c6658f4eb5dc81b1d559e6fa3c9f8a78284a1b490da6e9e87b5655587e666e10a6973f94d6647fef8f5cae425e1ade312dd9b37b807e547f76256e4191a8bc27e1ac88c0c1a037cbea830665c427521b11c30e050faf7ae1&separate_transkey_gb=e2e&isAjax=true&isGrid=true";

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
                Debug.setErrLog(SUBSYSTEM );
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
        System.exit(0);
    }
    
    private void operate(){
		try {
			URL url = null;
			BufferedReader in = null;
	        String inputLine = null;
	        StringBuffer response = null;
	        url = new URL(mUrl);
			
	        mHttpsConn = (HttpsURLConnection) url.openConnection();
	    	mHttpsConn.setRequestMethod("POST");
			mHttpsConn.setRequestProperty("Accept", "text/html, */*; q=0.01");
			mHttpsConn.setRequestProperty("Request URL", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp");
			mHttpsConn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,ja;q=0.6");
			mHttpsConn.setRequestProperty("Connection", "keep-alive");
			mHttpsConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mHttpsConn.setRequestProperty("Cookie", "ccguid=16b1c49760e-314b468060058f098; WMONID=VshF3jta2jK; tabLgnAtmtMvmn=OFRC; _INSIGHT_CK_5602=12436a08d3a840e032ca83ab2bf3e101_47801|e93c9d1c7b85108ae3288ac015003528_39705:1564041554000; _INSIGHT_CK_5603=a968b801af45c01b227f076efec2621e_47705|5b713307d3adf035b12c8baf77891779_66455:1564368255000; ccsession=16c40cb3ee6-327fef0c200cb5033; _INSIGHT_CK_5601=2714700955e8d130e2e4fd6b206293e7_89489|383f2b12e9c771472585f9bd7e71e823_55157:1564456958000; BIGipServer~mybank~pool_mybank_80=713205952.20480.0000; delfino.recentModule=G3; JSESSIONID=0000_iPDuYEmvszwKWggpgdvAFu:-1");
			mHttpsConn.setRequestProperty("Host", "mybank.ibk.co.kr");
			mHttpsConn.setRequestProperty("Origin", "https://mybank.ibk.co.kr");
			mHttpsConn.setRequestProperty("Sec-Fetch-Mode", "cors");
			mHttpsConn.setRequestProperty("Sec-Fetch-Site", "same-origin");
			mHttpsConn.setRequestProperty("Referer", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp");
			mHttpsConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3860.5 Safari/537.36");
			mHttpsConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			
			if(mHttpsConn.getResponseCode() == 200){
				
				in = new BufferedReader(new InputStreamReader(mHttpsConn.getInputStream()));
				response = new StringBuffer();
		        while ((inputLine = in.readLine()) != null) {
		        	if(inputLine.contains("RgUtil.setInsertRecords('grid_area', '")) {
		        		response.append(
		        				(inputLine.split("grid_area")[1]).split("'")[2]
		        		);
		        	}
		        }
		        in.close();
		        IBKParser ibkParser = new IBKParser();
		        Debug.trace(SUBSYSTEM, 0, "Reusult:"+ibkParser.parse(response.toString()));
			}else Debug.trace(SUBSYSTEM, 0, "ResponseCode:"+mHttpsConn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
			Debug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+"Not Available Yet !!");
		} finally {
		}
	}

	// Main Method
	public static void main(String[] args) {
		Crawler.initialize(args);
		Crawler wc = new Crawler();
		try {
			wc.start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	class ScheduleExecuteTask extends TimerTask {
		public void run(){
	    	if(!mShutdown){
	    		operate();
	    	}else{
	    		Debug.closeErrLog();
	    		System.exit(0);
	    	}
	    }
	}

}
