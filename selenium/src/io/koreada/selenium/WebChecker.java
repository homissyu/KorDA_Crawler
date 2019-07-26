/**
 *
 */
package io.koreada.selenium;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.koreada.util.UrlConnection;
import io.koreada.util.Debug;
import io.koreada.util.Install;

/**
 * @author user
 *
 */
public class WebChecker {

	private static String SUBSYSTEM = "Crawler4IBK";

    protected static Install mInstall = null;
    protected static Debug mDebug = null;

    private static WebChecker mWebChecker = null;

    public boolean mShutdown = false;
    private int iInterval = 0;
    private String mUrl = null;
    
    private boolean bFirstFlag = false;
    private int iRunningCnt = 0;

//	private SocketConnection mSock;
	private UrlConnection mUrlConnection;
	private HttpURLConnection mHttpUrlConnection;

    // Constructor
    public WebChecker() {
    }

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
//    	mUrl += "=T05&page=";
//    	mUrl = "http://search.gmarket.co.kr/search.aspx?selecturl=total&sheaderkey=&gdlc=&SearchClassFormWord=goodsSearch&keywordOrg=%BC%F8%B1%DD&keywordCVT=%BC%F8%B1%DD%2C%BC%F8%B1%DD%C6%C8%C2%EE%2C%BC%F8%B1%DD%B8%F1%B0%C9%C0%CC%2C%BC%F8%B1%DD%B9%DD%C1%F6&keywordCVTi=1&keyword=%BC%F8%B1%DD";
//        System.out.println("mUrl:"+mUrl);
    	
//    	mUrl = "https://fred.stlouisfed.org/series/GOLDAMGBD228NLBM";//FRED
//    	mUrl = "https://bank.shinhan.com/index.jsp#020700000000";//Shinhan
//    	mUrl = "https://spot.wooribank.com//pot/jcc?withyou=POGLD0025&__ID=c013858&BAS_DT=20190102";//Woori
    	
    	

    	
    	mUrl = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp?inq_stdd=20190127&inq_endd=20190726&cus_acn=05414414001019&inq_dcd=1&otpt_trtp_dcd=2&sevr_prn_rqst_cnt=&ipinsideData=%3Dv1IeAAsAVfUWfKj7uJFkz8i42aRHQXZvppXdPW3TK7Jl%2B%2FW8NzMeRjxn7YFeLj%2B%2B7%2BQSSzHHRSzvJ0vVAXZdEKVXE2Xj7%2Bw%2BwY6ZZHmVVBjcMbYPI7J4IXX5EdLEoZGTO4bulBoSCbypmzfZ39sXAfdLJ%2BSaxxzJyjPysj28qD64ZHCf97p17omBylGZG%2B6hm0%2F8uH0LBmNnrIu1VK9P6VH0%2BJ5TpeWL76pFRoqAGzXEqRIA8qVyPKHOOpOGW0AMQW3Tkzt7ziGCZxqkSnjvtyoYDwucoY3YBuXQbWnqzG6KJ6z7Te8evxnfhP9S2rx2C4f8HAre1WB7q5xagwo%2FrOR2mdDZs9Bwte21BOHL2QCIC%2B1PqJRr0BbrYlh3w0CZq%2FWoIJKAN0rnjckL2sQPWQW0D5i%2BmIAyO8ikHriMGO9UxGXcJxkmaIVd1vUp2BYy18vu7lZcKLIwUtRX9rdU97G3PxTY3rRwcKjACkeDM9h7FT7vaGCapLl6PJvQt7CGmXA1o0qfWwwfuNJxiP56lEFwx7caAt3Se5gkumPFgvOa5KAU16XVVD7oh%2Fq8ywfJSMgZFP%2B67Ts%2BFk1b&ipinsideNAT=xAk2HRloTCj7zhoosUkKabxvj4qETyEf885CeocX67NYB84224LAOvwM3StJTZw49y768CypiHq1uLkAGvXZHEnglLlAK4gFnTMcIiUeSfCITf6sAQ0DZscPOK7f1848Ma%2B9v4%2BjPZk6sIPJRZCNJIsKpBm6YkMWgk%2BFC2%2FcYQH%2BH7mdnSlyat9A2V9JgKfa&ipinsideCOMM=rPz9TJJd0pwzqbwkEMcVoBwR1b4FL24EJBCOobs%2FpJ1X8XO%2Fdbr%2BuUhYNjE8%2B6Amqdu8MLjy%2FrWCO4B6qOTvkSNQUSbYoeup5%2F7qPBG5FoHDkHgIgPNFeex5MDyruhwWPG%2B4b0Jr5mjLBJN1j4VchqMmDqRfhyXgrM2Rpr3g6zsJj0vv9ZxFJEQPRpiw24GJunWnolH2mFJXNCsNIB0krDNfXjD8bjiswxncDeOObPDuDlhXUigdR00xonBwwG02cqD89rmsW%2FnIL0ivgjiuKOler7oj9NxQmVEBZyestyf50f%2BXx5GuF4Mmv25SHYfoxokoqTV%2FZJaPoW4C%2B1cupKBPwTU%2FN3%2BZkdj7jvQrvdj1cKet%2B%2BZAknxzJCUiR0x%2BnkSZ1aCdByci6RKcFxHT54aWpu1FROAUpWIIkjVhdYB0n%2FPCONqfdZT3R%2FCfFb7nuhljfazf%2BUvsAVrHow7Tl7%3D%3D&W_SRVR_DATA_DLPR_EN=N&W_SRVR_DATA_DLPR=&pageIndex=1&rebound_yn=N&NEXT_PAGE_TRN_ID_2=&in_cus_acn=05414414001019&acnt_pwd=0409&rnno=8101460&rdo_inq_dcd=1&sel_otpt_trtp_dcd=2&inqy_sttg_ymd_yy=2019&inqy_sttg_ymd_mm=01&inqy_sttg_ymd_dd=27&inqy_sttg_ymdDateBoxType=period&inqy_sttg_ymdfromday=20140726%2F&inqy_sttg_ymdtoday=20190726%2F&inqy_sttg_ymd=2019-01-27&inqy_eymd_yy=2019&inqy_eymd_mm=07&inqy_eymd_dd=26&inqy_eymdDateBoxType=period&inqy_eymdfromday=20140726%2F&inqy_eymdtoday=20190726%2F&inqy_eymd=2019-07-26&_DUMMY_INPUT=&transkeyUuid=3d42fef2acd1e6346f493128cdb77308821835fc66564b1d6a76869cd5ca837e&transkey_acnt_pwd=%2448%2Ca5%2C59%2C54%2Cca%2C89%2C20%2Cd3%2C8e%2C6f%2C5e%2C8e%2Cac%2Cc4%2C32%2Ce%24f7%2C2a%2C9c%2C8e%2C86%2Cc5%2C32%2C91%2Cea%2Cb1%2Cf%2Cfe%2Cee%2Cf8%2Cda%2C34&transkey_HM_acnt_pwd=4e445dc666e62fce1c03d00ae7ba97531e3d943116daaafcb1a4589835475d58&Tk_acnt_pwd_separator=e2e&transkey_rnno=%24f4%2C2d%2C3c%2Cb8%2C89%2Ca2%2C59%2C39%2Cfb%2Cd4%2Cc8%2C37%2C0%2C44%2Cb5%2C33&transkey_HM_rnno=d70a68fe338e54a2ac1b681db83225e6ca5788866fd3a32389928d5ef58b6d1a&Tk_rnno_separator=e2e&hid_key_data=260c5e8d50fec49b37bb4261f3f67ace601c28f7b01e469be8c710478d831a3cdca3fdc289734cc59bdd7046d141b99f2039d125fd4ec01a234d2196e47c7dfab1c15dee7d2dbdc35bce605cf9189202ab24ab39b7c48f8cd8c8d384ff40305e71d23a62ee2579e80f10b5cfb40c326e7e489a224ea404fd7e6170f3ac3122ecf43f44dc6e5dcce58fd2104d1eeab3d9924841c5db35efe56bf727444dfab67ecbbeb6914b947358380bf4667e5ca48c574d7f4b89c1cb3fcea054b866cc7ff34bb237649928efeebb818cec8b9252cb2590109651fbcdb535ab48cda99606b022bcd0a1fc675c0d7077a8a7bbcfc4fa848c69267e76f1df87d01f5cefbd94dd&hid_enc_data=&E2E_acnt_pwd=a66c74cd52a2d48fd3d7c4fdcd7e6751c23bf1eb05daf42872dfd4d70b0a275498e587cf2acd1f9d5e25098f239afa8b2f48384ecf2f83e114de1af9d7801c82&E2E_rnno=658c15b7123f51069482a093ce530ba16ac4dbe01004275091374a9ede4466c5caae853fffb245fb8494599758a0e42d04d40f8a0ab14538992fd4fffa12392e9dbc62b5d4cddf3366a205f27d93678201262451915bac75be4b7750e27bb16789f7d483651cb213c9ece22dede00272&separate_transkey_gb=e2e&isAjax=true&isGrid=true";
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
    
    
    
    
    // ���� ���ϴ� �޼ҵ� �Դϴ�.
    private void operate(){
//    	Debug.trace(SUBSYSTEM, 0,"");
//    	Debug.trace(SUBSYSTEM, 0,"");
//		Debug.trace(SUBSYSTEM, 0,"==============================================");
//		Debug.trace(SUBSYSTEM, 0,"					Start WebCheck!!");
//		Debug.trace(SUBSYSTEM, 0,"");
//		Debug.trace(SUBSYSTEM, 0,"http://"+mInstall.getProperty(Install.SMART_BRIDGE_IP)+":"+Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_PORT)));
//		Debug.trace(SUBSYSTEM, 0,"==============================================");
		try {
//			mUrlConnection.connect(mUrlConnection.getRealIpAddress(mInstall.getProperty(Install.SMART_BRIDGE_IP)),Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_PORT)));
			URL url = null;
			int iPage = 1;
//			mUrl += "=T05&page=";
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
//				targetUrl = mUrl + Integer.toString(iPage); 
				targetUrl = mUrl;
				url = new URL(targetUrl);
				
				mHttpUrlConnection = (HttpURLConnection)url.openConnection();
				
//				mHttpUrlConnection.setRequestProperty("Host", "mybank.ibk.co.kr");
//				mHttpUrlConnection.setRequestProperty("Connection", "keep-alive");
//				mHttpUrlConnection.setRequestProperty("Content-Length", "4421");
//				mHttpUrlConnection.setRequestProperty("Accept", "text/html, */*; q=0.01");
//				mHttpUrlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//				mHttpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3860.5 Safari/537.36");
//				mHttpUrlConnection.setRequestProperty("Sec-Fetch-Mode", "cors");
//				mHttpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//				mHttpUrlConnection.setRequestProperty("Origin", "https://mybank.ibk.co.kr");
//				mHttpUrlConnection.setRequestProperty("Sec-Fetch-Site", "same-origin");
//				mHttpUrlConnection.setRequestProperty("Referer", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp?width=900&height=680&scroll=yes");
//				mHttpUrlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//				mHttpUrlConnection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
//				mHttpUrlConnection.setRequestProperty("Cookie", "WMONID=NciHGavcSDe; BIGipServer~mybank~pool_mybank_80=713205952.20480.0000; ccsession=16c2bbed9a5-314b4660600f3e2a8; ccguid=16c2bbed9a5-314b4660600f3e2a8; delfino.recentModule=G3; _INSIGHT_CK_5602=5c8cbd1f3fc5b0b1b2b1417ab5c3b090_13415|3e74271f0feef1d3fac1417ab5c3b090_13415:1564115230000; JSESSIONID=0000NpntpGmDjs_pcpXFBGQbPVp:-1");
				
				
				
				if(mHttpUrlConnection.getResponseCode() == 200 && iPage<=1){
					System.out.println(targetUrl);
					
					InputStream is = mHttpUrlConnection.getInputStream();
					byte [] b = new byte[1024];
					int len = 0;
					while( (len = is.read(b, 0, b.length)) != -1){
						tempContentsBuf.append(new String(b,0,len));						
					}
//					iRunningCnt++;
//					System.out.println(iRunningCnt);
//					if(iRunningCnt >= 2000)mShutdown = true;
					
					tempDoc = Jsoup.parse(tempContentsBuf.toString());
					System.out.println(tempDoc);
//					Elements divTblBox = tempDoc.getElementsByAttributeValue("class", "tbl_box");
//					if(iPage==1) {
//						tHead = tempDoc.select("thead");
//						Debug.trace(SUBSYSTEM, 0,"<HTML><BODY><TABLE>"+tHead.toString());
//					}
//					tBody = tempDoc.select("body");
//					tListContainer = tempDoc.getElementById("item_info");
					iTemInfo = tempDoc.getElementsByAttributeValue("class", "title");//Gmarket Info
					priceInfo = tempDoc.getElementsByAttributeValue("class", "price");//Gmarket Price
//					goldPriceInfo = tempDoc.getElementsByAttributeValue("class", "series-meta-observation-value");//FRED
					goldPriceInfo = tempDoc.getElementsByAttributeValue("id", "grd_list_datalayer");//Shinhan
//					goldPriceInfo = tempDoc.getElementsByAttributeValue("class","tbl-type-1.ui-set-tbl-type");//Woori
					System.out.println("goldPriceInfo:"+goldPriceInfo);
//					iTemInfo.
					iPage++;					
				}else break;								
			}
			/*
			HashMap<Integer, ArrayList<Number>> goodsMap = new HashMap<Integer, ArrayList<Number>>();
			
			
			String infoPattern = "\\d[0-9,\\.]*g";
			Pattern infoPat = Pattern.compile(infoPattern);
			
			String pricePattern = "\\d[0-9,\\,]*";
			Pattern pricePat = Pattern.compile(pricePattern);
			
			Matcher infoMatcher = null;
			Matcher priceMatcher = null;
			
			
			for(int i=0;i<iTemInfo.size();i++) {
				infoMatcher = infoPat.matcher(iTemInfo.get(i).text());
				priceMatcher = pricePat.matcher(priceInfo.get(i).text());
				
				ArrayList<Number> goodList = new ArrayList<Number>();
				while(infoMatcher.find()){ 
					float tempAmount = Float.parseFloat(infoMatcher.group(0).replaceAll("g", ""));
					goodList.add(tempAmount);
					while(priceMatcher.find()){
						int tempPrice = Integer.parseInt(priceMatcher.group(0).replaceAll(",", ""));
						goodList.add(tempPrice);
						goodList.add(tempPrice/tempAmount);
					}
				}
				if(!goodList.isEmpty())goodsMap.put(i, goodList);
			}
			System.out.println(goodsMap);
				
			Debug.trace(SUBSYSTEM, 0,new String((iTemInfo.text()).getBytes("EUC-KR"),"UTF-8")+"</TABLE></BODY></HTML>");
			*/
			shutdown();
			
//			Debug.trace(SUBSYSTEM, 0,"getContentType()  : " +  mHttpUrlConnection.getContentType());
//    		Debug.trace(SUBSYSTEM, 0,"getContentLength() : " + mHttpUrlConnection.getContentLength());
//    		Debug.trace(SUBSYSTEM, 0,"getUseCaches() : " + mHttpUrlConnection.getUseCaches());
//    		Debug.trace(SUBSYSTEM, 0,"getRequestMethod() : " + mHttpUrlConnection.getRequestMethod());
//    		Debug.trace(SUBSYSTEM, 0,"getResponseCode() : " + mHttpUrlConnection.getResponseCode());
//    		Debug.trace(SUBSYSTEM, 0,"getResponseMessage() : " + mHttpUrlConnection.getResponseMessage());
//    		Map aTempMap = mHttpUrlConnection.getHeaderFields();
////    		Iterator it = aTempMap.keySet().iterator();
//    		Debug.trace(SUBSYSTEM, 0,"aTempMap.toString() : " + aTempMap.toString());

//    		Debug.trace(SUBSYSTEM, 0,"==============================================");
    		
    		
		} catch (Exception e) {
			e.printStackTrace();
			Debug.trace(SUBSYSTEM, 0,"Not Available Yet !!");
		} finally {
//			Debug.trace(SUBSYSTEM, 0,"==============================================");
//    		Debug.trace(SUBSYSTEM, 0,"					End WebCheck!!");
//    		Debug.trace(SUBSYSTEM, 0,"==============================================");
		}
	}

	// Main Method
	public static void main(String[] args) {
		WebChecker.initialize(args);
		WebChecker wc = new WebChecker();
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
