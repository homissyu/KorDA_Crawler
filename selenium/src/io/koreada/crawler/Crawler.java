/**
 *
 */
package io.koreada.crawler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

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
    
	private HttpsURLConnection mHttpsConn = null;
	
	private IBK ibkParser = new IBK();
	private int mOldHashCode = 0;
	private int mNewHashCode = 0;
	
	private ObjectMapper mapper = new ObjectMapper();
	private String aFileName = CommonConst.ACCOUNT_INFO_NAME + CommonConst.CURRENT_DIR + CommonConst.JSON_EXTENSION;
	private JsonGenerator jg = null;
	
    // Constructor
    public Crawler() throws FileNotFoundException, IOException {
    	jg = mapper.getFactory().createGenerator(
    			new FileOutputStream(
    					new File(
    							CommonConst.CURRENT_DIR + File.separator + CommonConst.LOGS_DIR + File.separator + aFileName)
    					)
    			);
    }

    // Initial Start Method
    @SuppressWarnings("deprecation")
	public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
    	mUrl = URLDecoder.decode(mUrl);
//    	mUrl = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp";
    	
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
        System.exit(0);
    }
    
    private ArrayList<?> operate(){
    	ArrayList ret = null;
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
			mHttpsConn.setRequestProperty("Cookie", "ccguid=16b1c49760e-314b468060058f098; WMONID=VshF3jta2jK; tabLgnAtmtMvmn=OFRC; _INSIGHT_CK_5603=a968b801af45c01b227f076efec2621e_47705|5b713307d3adf035b12c8baf77891779_66455:1564368255000; BIGipServer~mybank~pool_mybank_80=696428736.20480.0000; ccsession=16c43292bcb-314b4660602342015; delfino.recentModule=G4; Veraport20Use=N; npPfsHost=127.0.0.1; npPfsPort=14440; _INSIGHT_CK_5602=12436a08d3a840e032ca83ab2bf3e101_47801|7694931091ff20b4318d4204362f4e01_94879:1564496859000; _vp_object_load=N; _INSIGHT_CK_5601=2714700955e8d130e2e4fd6b206293e7_89489|37b03720c5ace0fba9685470e297736d_07059:1564708861000; JSESSIONID=0000xxFY2am18aR8vGhD0nb7can:-1");
			mHttpsConn.setRequestProperty("Host", "mybank.ibk.co.kr");
			mHttpsConn.setRequestProperty("Origin", "https://mybank.ibk.co.kr");
			mHttpsConn.setRequestProperty("Sec-Fetch-Mode", "cors");
			mHttpsConn.setRequestProperty("Sec-Fetch-Site", "same-origin");
			mHttpsConn.setRequestProperty("Referer", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp");
			mHttpsConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3860.5 Safari/537.36");
			mHttpsConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			
			mHttpsConn.setDoInput(true);
			mHttpsConn.setDoOutput(true);
			mHttpsConn.setUseCaches(false);
			
			
			DataOutputStream dos = new DataOutputStream(mHttpsConn.getOutputStream());
			dos.writeBytes(mParam);
			dos.flush();
			dos.close();
			
			if(mHttpsConn.getResponseCode() == 200){
				
				in = new BufferedReader(new InputStreamReader(mHttpsConn.getInputStream()));
				response = new StringBuffer();
		        while ((inputLine = in.readLine()) != null) {
		        	if(inputLine.contains("RgUtil.setInsertRecords('grid_area', '")) {
		        		response.append(
		        				(inputLine.split("grid_area")[1]).split("'")[2]
		        		);
		        	}else if(inputLine.contains("\"result\":\"error\"")) throw new Exception("Exception Occured! : "+inputLine);
		        }
		        in.close();

		        ret = ibkParser.parse(response.toString());
		        mNewHashCode = ibkParser.getHashCode();
		        if(mOldHashCode == mNewHashCode) {
		        	ret.removeAll(ret);
		        	ret.add(new String("No Change"));
		        }
		        mOldHashCode = mNewHashCode;
		        

		        
			}else Debug.trace(SUBSYSTEM, 0, "ResponseCode:"+mHttpsConn.getResponseCode());
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.trace(SUBSYSTEM, 0,e.getLocalizedMessage()+" Not Available Yet !!");
		} finally {
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
