/**
 *
 */
package io.koreada.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.koreada.executer.Executer;
import io.koreada.executer.ExecuterIBK;
import io.koreada.parser.HashCodeList;
import io.koreada.supportfactory.APIFactory;
import io.koreada.util.CommonConst;
import io.koreada.util.CommonUtil;
import io.koreada.util.Debug;
import io.koreada.util.FileHandler;
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
	private String aAccountFileName = CommonConst.ACCOUNT_INFO_NAME + CommonConst.CURRENT_DIR + CommonConst.JSON_EXTENSION;
	private JsonGenerator jg = null;
	
	private Executer mExecuter = null;
	
	private int mExecuteBank = 0;
	
	private String aAccountInfoFilePath = mInstall.getLogDir() + File.separator +aAccountFileName;
	
    // Constructor
    public Crawler() throws Exception {
    	if(!FileHandler.isEmptyFile(aAccountInfoFilePath)) {
    		FileHandler.copyFile(aAccountInfoFilePath,  mInstall.getLogDir() + File.separator + CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT) + CommonConst.CURRENT_DIR + aAccountFileName);
    	}
    	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    	jg = mapper.getFactory().createGenerator(new FileOutputStream(new File(mInstall.getLogDir() + File.separator + aAccountFileName)));
    }
    
        // Initial Start Method
    private void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	mExecuteBank = Integer.parseInt(mInstall.getProperty(Install.SMART_BRIDGE_BANK));
    	HashCodeList aBackupHashCodeList = null;
    	File aFile = new File(mInstall.getRootDir() + File.separator + CommonConst.BACKUP_FILE_NAME);
		if(aFile.exists()) {
			aBackupHashCodeList = readHashCodeListFile();
//    		System.out.println((this.mOldHashCodeList.getHashCodeList()).toString());
    	}
    	switch(mExecuteBank) {
	    	case 0:
	    		mExecuter = new ExecuterIBK(mDebug, mInstall, aBackupHashCodeList);
	    		break;
	    	default:
    			mExecuter = new ExecuterIBK(mDebug, mInstall, aBackupHashCodeList);
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
                Debug.setErrLog(CommonConst.LOG_FILE_NAME);
                mApi = new APIFactory(mInstall);
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
        mExecuter.closeDriver();
        System.exit(0);
    }
    
    private void writeHashCodeListFile(HashCodeList hashCodeList) {
		FileHandler.writeSerFile(hashCodeList, mInstall.getRootDir(), CommonConst.BACKUP_FILE_NAME);
	}
	
	private HashCodeList readHashCodeListFile() {
		return (HashCodeList)FileHandler.readSerFile(mInstall.getRootDir() + File.separator + CommonConst.BACKUP_FILE_NAME);
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
			Debug.traceError(SUBSYSTEM, e,e.getLocalizedMessage());
			wc.mExecuter.closeDriver();
		}
    }

	class ScheduleExecuteTask extends TimerTask {
		private ArrayList<?> obj = null;
		public void run(){
			try {	
		    	if(!mShutdown){
		    		int iMode = Integer.parseInt((mInstall.getProperty(Install.SMART_BRIDGE_CRAWLER_TYPE)));
		    		System.out.println("iMode:"+iMode);
		    		switch(iMode) {
			    		case CommonConst.OPERATION_TOTAL:
			    			operateSchedule(CommonConst.OPERATION_DEPOSIT);
			    			operateSchedule(CommonConst.OPERATION_WITHDRAW);
			    			break;
			    		case CommonConst.OPERATION_WITHDRAW:
			    			operateSchedule(CommonConst.OPERATION_WITHDRAW);
			    			break;
			    		case CommonConst.OPERATION_DEPOSIT:
			    			operateSchedule(CommonConst.OPERATION_DEPOSIT);
			    			break;
		    			default:
		    				operateSchedule(CommonConst.OPERATION_DEPOSIT);
		    				operateSchedule(CommonConst.OPERATION_WITHDRAW);
			    			break;
		    		}
		    	}else{
		    		jg.close();
		    		shutdown();
		    	}
			}catch(Exception ex) {
				ex.printStackTrace();
				Debug.traceError(SUBSYSTEM, ex,ex.getLocalizedMessage());
			} 
	    }
		
		private void operateSchedule(int iMode) throws JsonGenerationException, JsonMappingException, IOException, InterruptedException {
			obj = mExecuter.operate(iMode);
    		System.out.println(obj);
//    		System.out.println(obj.isEmpty());
//    		System.out.println(obj.size());
    		if(Install.RESULT_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_TYPE))
    				|| !obj.isEmpty()) {
    			if(Install.RESULT_LOG_TYPE_DEFAULT.equals(mInstall.getProperty(Install.RESULT_LOG_TYPE))) {
    				mapper.writeValue(jg, obj);
    				jg.writeRaw(System.lineSeparator());
		    	}else {
		    		mapper.writeValue(new File(CommonConst.CURRENT_DIR + File.separator + CommonConst.LOGS_DIR + File.separator + CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+ CommonConst.CURRENT_DIR + aAccountFileName), obj);
		    	}
    			if(1 == Integer.parseInt(mInstall.getProperty(Install.OPERATION_MODE)) && mApi.deposit2API(mapper.writeValueAsString(obj))) writeHashCodeListFile(mExecuter.getHashCodeList());
    		}
    		Thread.sleep(100);
		}
	}

}
