/**
 *
 */
package io.koreada.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.koreada.executer.Executer;
import io.koreada.executer.ExecuterIBK;
import io.koreada.executer.ExecuterNH;
import io.koreada.supportfactory.APIFactory;
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
    protected static APIFactory mApi = null;

    public boolean mShutdown = false;
    
    private int iInterval = 0;
    
    private ObjectMapper mapper = new ObjectMapper();
	private String aFileName = CommonConst.ACCOUNT_INFO_NAME + CommonConst.CURRENT_DIR + CommonConst.JSON_EXTENSION;
	private JsonGenerator jg = null;
	
	private Executer mExecuter = null;
	
	private int mExecuteBank = 0;
    
    // Constructor
    public Crawler() throws FileNotFoundException, IOException {
    	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    	jg = mapper.getFactory().createGenerator(new FileOutputStream(new File(mInstall.getLogDir() + File.separator + aFileName)));
	}
    
        // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	mExecuteBank = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_BANK));
    	
    	switch(mExecuteBank) {
	    	case 0:
	    		mExecuter = new ExecuterIBK(mDebug, mInstall);
	    		break;
	    	case 1:
	    		mExecuter = new ExecuterNH(mDebug, mInstall);
	    		break;
    		default:
    			mExecuter = new ExecuterIBK(mDebug, mInstall);
    			break;
    	}
    	
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
                mApi = new APIFactory(mInstall);
            } catch (Exception e) {
            	mInstall = null;
            	mDebug = null;
            	mDebug.traceError(SUBSYSTEM, e, "Exception occured : "+e.getLocalizedMessage());
            	System.exit(1);
            }
        }
    }

    // Shutdown Method, Connection disconnect, System.exit
    public void shutdown() {
    	mDebug.trace(SUBSYSTEM, 1, "Shutting down....");
    	mShutdown = true;
    	mDebug.closeErrLog();
        mExecuter.closeDriver();
        System.exit(0);
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
			mDebug.trace(SUBSYSTEM, 0,e.getLocalizedMessage());
			wc.mExecuter.closeDriver();
		}
    }

	class ScheduleExecuteTask extends TimerTask {
		private ArrayList<?> obj = null;
		public void run(){
			try {	
		    	if(!mShutdown){
		    		obj = mExecuter.operate();
//		    		System.out.println(obj);
//		    		System.out.println(obj.isEmpty());
//		    		System.out.println(obj.size());
		    		if(Install.RESULT_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_TYPE))
		    				|| !obj.isEmpty()) {
		    			if(Install.RESULT_LOG_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_LOG_TYPE))) {
		    				mapper.writeValue(jg, obj);
		    				mApi.send2API(mapper.writeValueAsString(obj));
		    				jg.writeRaw(System.lineSeparator());
				    	}else {
				    		mapper.writeValue(new File(CommonConst.CURRENT_DIR + File.separator + CommonConst.LOGS_DIR + File.separator + CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+ CommonConst.CURRENT_DIR + aFileName), obj);
				    	}
		    		}
		    	}else{
		    		jg.close();
		    		shutdown();
		    	}
			}catch(Exception ex) {
				ex.printStackTrace();
				mDebug.trace(SUBSYSTEM, 0,ex.getLocalizedMessage());
			} 
	    }		
	}

}
