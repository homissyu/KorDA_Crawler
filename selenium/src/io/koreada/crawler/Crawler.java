/**
 *
 */
package io.koreada.crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import io.koreada.parser.IBK;
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
    
	private HttpsURLConnection mHttpsConn = null;
	
	private IBK ibkParser = new IBK();

    // Constructor
    public Crawler() {
    }

    // Initial Start Method
    @SuppressWarnings("deprecation")
	public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
    	mUrl = URLDecoder.decode(mUrl);
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
		        ibkParser.parse(response.toString());
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
