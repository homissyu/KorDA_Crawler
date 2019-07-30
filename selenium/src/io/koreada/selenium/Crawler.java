/**
 *
 */
package io.koreada.selenium;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.koreada.util.Debug;
import io.koreada.util.Install;
import io.koreada.util.UrlConnection;

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
    
    private boolean bFirstFlag = false;
    private int iRunningCnt = 0;

//	private SocketConnection mSock;
	private UrlConnection mUrlConnection;
	private HttpURLConnection mHttpUrlConnection;

    // Constructor
    public Crawler() {
    }

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
    	mUrl = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-origin' -H 'Origin: https://mybank.ibk.co.kr' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,ja;q=0.6' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3860.5 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Accept: text/html, */*; q=0.01' -H 'Referer: https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp' -H 'X-Requested-With: XMLHttpRequest' -H 'Cookie: ccguid=16b1c49760e-314b468060058f098; WMONID=VshF3jta2jK; tabLgnAtmtMvmn=OFRC; _INSIGHT_CK_5602=12436a08d3a840e032ca83ab2bf3e101_47801|e93c9d1c7b85108ae3288ac015003528_39705:1564041554000; ccsession=16c3b81bf51-314b468060027a0a2; _INSIGHT_CK_5603=a968b801af45c01b227f076efec2621e_47705|5b713307d3adf035b12c8baf77891779_66455:1564368255000; _INSIGHT_CK_5601=2714700955e8d130e2e4fd6b206293e7_89489|ea7d5f05afeae0c27134214ae449c559_78301:1564380103000; JSESSIONID=0000qdvLrOdfGt9i08063iVXOI-:-1; BIGipServer~mybank~pool_mybank_80=713205952.20480.0000; delfino.recentModule=G3' -H 'Connection: keep-alive' --data 'inq_stdd=20190130&inq_endd=20190729&cus_acn=05414414001019&inq_dcd=1&otpt_trtp_dcd=2&sevr_prn_rqst_cnt=&ipinsideData=%3Dv1IeAEbJXpbOQupoENgvuYS%2BCjf6GODl8X8KUyNdgMkdYVE%2FNKbW5qxMCWwFncn8V9t7DqGQoJL1ZschWBnwswk4JN%2BOQ8OUPOmG9MeoR%2F1mWhmXExlhtSgL0qTctulSJ%2BvJKwYz%2BcvuyugKMi7hlK6rX%2B7wHNKFYVdhq1LBUZuq5yC5MBNi18MUwW70XAvRIfCpsfqIqQbCD6ha3TZ5jnHtOuI%2FJKF%2B4EvMeIGWXiN6sFOwm4CdlcLQR9MgN%2B9%2FmZ%2BnkAI8GnkxaKxIDUN%2BfvT%2FuPhfzOKq9leZUUOKNYktMHwUj3HjN5TFgWCbNmO0cPecqnWTi2UDz9eQQ1JVV0jjHbc%2F6W3hmA3TqNAnSLU5qPLP6N%2Bt7BzME%2BJfRXTdwtNhahTQidDzhiBIgvhwRdXPDVM7Z1tpGIeCDl3seeOuiItvUoobvbEJVT%2FcolVozzX42vNPzcLp%2Bbs2SAHKv1mC3XzqtMSIvW8rRWr1SQVjflKm0K6cM%2BdCH3TYSsXIkAW6f6jnr2yICgMBMoIt1Td6ceVAWLi7eA9ROSEoZ0xSWYJ2Q%2BeTOD2i7Kp0BVcpNtqQbJtgyWuxIJar&ipinsideNAT=xAk2HRloTCj7zhoosUkKabxvj4qETyEf885CeocX67M%2FujwkOSf%2Bpq6vBRNJZtqPqVJ9%2B0bt4i9vwkGt%2BtdsTYEC9WwH4R7g1Ec%2BPIdnIIx3JdOk3TTwh4TAXnZ1ViyX%2Fp7OPANq5mXfwZQ6c1N7rRmmNAK0bnEIC2pqMddejGGqAhAK%2BGLkXDgBf%2FP%2Flezp&ipinsideCOMM=rPz9TJJd0pwzqbwkEMcVoBwR1b4FL24EJBCOobs%2FpJ1X8XO%2Fdbr%2BuUhYNjE8%2B6Amqdu8MLjy%2FrWCO4B6qOTvkSNQUSbYoeup5%2F7qPBG5FoHDkHgIgPNFeex5MDyruhwWPG%2B4b0Jr5mjLBJN1j4VchqMmDqRfhyXgrM2Rpr3g6zsJj0vv9ZxFJEQPRpiw24GJunWnolH2mFJXNCsNIB0krDNfXjD8bjiswxncDeOObPDuDlhXUigdR00xonBwwG02cqD89rmsW%2FnIL0ivgjiuKOler7oj9NxQmVEBZyestydvP9%2FHyuPyxmxW%2B4J%2BpiY365wUx44goV%2FAWuoqQN%2FnbIrkmn2Yj7VlAUdfpUfbl0Hdto24qwVnfrQI%2BrBQ0K6moA9JTJChKAaKLy8ptj%2BCnCqWM9gBJRABkCW5oCSx38%2FuLM5ScyzxKPOKCmcljqcF2GHncZ8V3PjAH1NKdtbw9w%3D%3D&W_SRVR_DATA_DLPR_EN=N&W_SRVR_DATA_DLPR=&pageIndex=1&rebound_yn=N&NEXT_PAGE_TRN_ID_2=&in_cus_acn=05414414001019&acnt_pwd=0000&rnno=0000000&rdo_inq_dcd=1&sel_otpt_trtp_dcd=2&inqy_sttg_ymd_yy=2019&inqy_sttg_ymd_mm=01&inqy_sttg_ymd_dd=30&inqy_sttg_ymdDateBoxType=period&inqy_sttg_ymdfromday=20140729%2F&inqy_sttg_ymdtoday=20190729%2F&inqy_sttg_ymd=2019-01-30&inqy_eymd_yy=2019&inqy_eymd_mm=07&inqy_eymd_dd=29&inqy_eymdDateBoxType=period&inqy_eymdfromday=20140729%2F&inqy_eymdtoday=20190729%2F&inqy_eymd=2019-07-29&_DUMMY_INPUT=&transkeyUuid=c5a709e23afa765c9f89982c90125ed61bd013e75dfe9166eea8477b6745bf31&transkey_acnt_pwd=%2435%2C4b%2C8%2Cd9%2C43%2Ca2%2Cc5%2C70%2C68%2C5f%2C30%2C32%2C36%2Cae%2Ccb%2C36%24eb%2C30%2Ca0%2Ca2%2Cd8%2Cc2%2C4a%2Ccf%2Ccd%2C70%2Ce9%2Cc%2C86%2Cca%2C6e%2Ca%248c%2C2a%2Cdc%2C47%2Cfa%2C26%2C2e%2Cc9%2C0%2C6%2C37%2C46%2C5f%2C2b%2C86%2C3c%2429%2Cb0%2Ce5%2Cd3%2Ce0%2Cff%2C7d%2Cfe%2C90%2C49%2Cf4%2Cab%2C78%2C88%2Cf2%2C1a%249d%2Cfc%2Cff%2C11%2Ce8%2Cd8%2Cab%2Cf8%2Cfd%2C6a%2C65%2C54%2C28%2C8a%2C6f%2C64&transkey_HM_acnt_pwd=fe7038d4d9a77f9d28339cc2c2a42ef402be3ffb4ed773fc44d828693829a7fa&Tk_acnt_pwd_separator=e2e&transkey_rnno=%24da%2C40%2C5%2C46%2C27%2C71%2C5e%2Cb3%2C4b%2C8%2Ca0%2C61%2C3a%2Cf8%2C14%2Cce%2465%2C2%2Ce5%2C57%2Cb6%2Cda%2C10%2Cc7%2C6c%2Cf9%2C14%2C24%2C81%2C91%2C51%2Caa%243c%2C38%2C72%2C2a%2C4b%2C36%2C1c%2Cf2%2Cb8%2C60%2C3a%2C76%2C2e%2Cd5%2C54%2C20%2429%2Cb0%2Ce5%2Cd3%2Ce0%2Cff%2C7d%2Cfe%2C90%2C49%2Cf4%2Cab%2C78%2C88%2Cf2%2C1a&transkey_HM_rnno=ca18fc7e32b6d10881fc6d68666af1e2408d1d726ded1192181fb6194ddd9288&Tk_rnno_separator=e2e&hid_key_data=a4b9aefe2251a5255fa8f660a0ef56904992a146e9ca0dec5aa19043d57771372611a2f3d1fb76fdd81fe80397d593c239fa7bb1de82dd0f2c50629b8a1a8306a18983e0c00255411f18617b96a41567e8c48b4d3a2872b9c4d741565fc06713cd9c11306236e28d4aae49c76efdf69801013623f395598666ce1f366f3ee93d2926d37ebc13129346e1c1b762a03fe014a0d567390e8f91836a0e3974c0ee93cde88d3c2b7e84683efa423697a063e33adee0fa8abcaf4f3ac8d01a00757b793542f02ae27e8ee110ecb64c9884e92bcfbd51cf81018d594dbd1efff1d4d064a836d8ad3bad9364d793e49ad1be9d04d03271f4edca217808baab897ab6ca0a&hid_enc_data=&E2E_acnt_pwd=79b642ff41814ef5ecd64c426d1675e700436f7c156637b829e2684e1a8458c02a6937351f2382937e2d0d5a764be757c57a1f2ce4a7e963235157660887916d&E2E_rnno=355e9a884320f7be38e7f41d210a55bd6556fa3e045e2e9b1a0223bfa1ace8e29e188d13a2f69ca457ceec632b518ec09aa243560646ed5ae7280a054e0b0dadf82d58ba154005f63c27bb1807ef9f64ed0509a79801cc814821e41360b0fcfa4f8d6b4109f6d06134995ec6530deedf&separate_transkey_gb=e2e&isAjax=true&isGrid=true";
    	mUrlConnection = UrlConnection.getInstance();
    	
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
			ProcessBuilder pb = new ProcessBuilder();
			
			
			
			URL url = null;
			int iPage = 1;
			String targetUrl = null;
			StringBuffer tempContentsBuf = new StringBuffer();
			Document tempDoc = null;
			Elements tHead = null;
			Elements tBody = null;
			Element tListContainer = null;
			Elements iTemInfo = null;
			Elements priceInfo = null;
			Elements goldPriceInfo = null;
			while(true) {
				targetUrl = mUrl;
				url = new URL(targetUrl);
				
				mHttpUrlConnection = (HttpURLConnection)url.openConnection();

				System.out.println(mHttpUrlConnection.getResponseCode());
				
				if(mHttpUrlConnection.getResponseCode() == 200 && iPage<=1){
					System.out.println(targetUrl);
					
					InputStream is = mHttpUrlConnection.getInputStream();
					byte [] b = new byte[1024];
					int len = 0;
					while( (len = is.read(b, 0, b.length)) != -1){
						tempContentsBuf.append(new String(b,0,len));						
					}

					tempDoc = Jsoup.parse(tempContentsBuf.toString());
					System.out.println(tempDoc);
					iTemInfo = tempDoc.getElementsByAttributeValue("class", "title");//Gmarket Info
					priceInfo = tempDoc.getElementsByAttributeValue("class", "price");//Gmarket Price
					goldPriceInfo = tempDoc.getElementsByAttributeValue("id", "grd_list_datalayer");//Shinhan
					System.out.println("goldPriceInfo:"+goldPriceInfo);
					iPage++;					
				}else break;								
			}
			shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.trace(SUBSYSTEM, 0,"Not Available Yet !!");
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
