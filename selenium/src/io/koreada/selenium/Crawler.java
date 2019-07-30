/**
 *
 */
package io.koreada.selenium;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.koreada.util.Debug;
import io.koreada.util.Install;
import io.koreada.util.UrlConnection;

import javax.net.ssl.HttpsURLConnection;

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
	private HttpsURLConnection mHttpsConn = null;

    // Constructor
    public Crawler() {
    }

    // Initial Start Method
    public void start(String[] args) throws Exception {
    	iInterval = Integer.parseInt(mInstall.getProperty(Install.DAEMON_INTERVAL));
    	mUrl = mInstall.getProperty(Install.SMART_BRIDGE_IP);
    	mUrl = "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp?inq_stdd=20190131&inq_endd=20190730&cus_acn=05414414001019&inq_dcd=1&otpt_trtp_dcd=2&sevr_prn_rqst_cnt=&ipinsideData=%3Dv1IeAEeFfVkOgFfDcXv0S9aM6ZpkWWIP1FtN86yoplQ1GWgre0gxLleH1ytYj34XdL%2BgulbKeq%2FcxknkfVHvrGamIvtT2Sc1XkgyqwIgN%2FY%2FQRdYd%2BSuC1L1pGQ0ZSGmy6Oozr6ZigwzF8b%2FBx8Edo10sH3ML6%2FiHV3%2FJQ%2BSHjzgsWpLBeFUehBcs9JGpPx80nOuvyRo4zMp37EOsdyBpk74x%2F1hjie4PxAj%2Bki9dZiwto%2BIalcncb4MxchCsV3MEDAyn6gM%2FBV1nMiHeIm4AcOBxn0uYmxXRuClQv3GGQ37C%2FwfzW0emDMuWrcJaAylSJz8wFRQaDelMLLyLadUDaMFa%2Ftqm19vibvyBqzdPfAIJHje4x6b1%2B7wigUPUq8Uo4qcuMuwWlu8qa0MU%2Bkj92F4pX3YI%2BhgN49ybqAQ8jEapDqgbUeqT8D5pDtyCIhk%2FphUAO78u46IFpVoI9EWm8rIwheSrnDckJla7mv3jweY6BoFFf4HS2AWn888PlG6yMR6xLrczAXVxi7wQKMnq%2B39hhhHT6%2F5eeg3y2UNXQi6i8qCAcv6NupT9UoEn8oXG1htL10L91Peh7l%2B&ipinsideNAT=xAk2HRloTCj7zhoosUkKabxvj4qETyEf885CeocX67NYB84224LAOvwM3StJTZw49y768CypiHq1uLkAGvXZHEnglLlAK4gFnTMcIiUeSfCITf6sAQ0DZscPOK7f1848Ma%2B9v4%2BjPZk6sIPJRZCNJIsKpBm6YkMWgk%2BFC2%2FcYQH%2BH7mdnSlyat9A2V9JgKfa&ipinsideCOMM=rPz9TJJd0pwzqbwkEMcVoBwR1b4FL24EJBCOobs%2FpJ1X8XO%2Fdbr%2BuUhYNjE8%2B6Amqdu8MLjy%2FrWCO4B6qOTvkSNQUSbYoeup5%2F7qPBG5FoHDkHgIgPNFeex5MDyruhwWPG%2B4b0Jr5mjLBJN1j4VchqMmDqRfhyXgrM2Rpr3g6zsJj0vv9ZxFJEQPRpiw24GJunWnolH2mFJXNCsNIB0krDNfXjD8bjiswxncDeOObPDuDlhXUigdR00xonBwwG02cqD89rmsW%2FnIL0ivgjiuKOler7oj9NxQmVEBZyestydvP9%2FHyuPyxmxW%2B4J%2BpiY365wUx44goV%2FAWuoqQN%2FnbIrkmn2Yj7VlAUdfpUfbl0Hdto24qwVnfrQI%2BrBQ0K6moA9JTJChKAaKLy8ptj%2BCnCqWM9gBJRABkCW5oCSx38%2FuLM5ScyzxKPOKCmcljqcF2GHncZ8V3PjAH1NKdtbw9w%3D%3D&W_SRVR_DATA_DLPR_EN=N&W_SRVR_DATA_DLPR=&pageIndex=1&rebound_yn=N&NEXT_PAGE_TRN_ID_2=&in_cus_acn=05414414001019&acnt_pwd=0000&rnno=0000000&rdo_inq_dcd=1&sel_otpt_trtp_dcd=2&inqy_sttg_ymd_yy=2019&inqy_sttg_ymd_mm=01&inqy_sttg_ymd_dd=31&inqy_sttg_ymdDateBoxType=period&inqy_sttg_ymdfromday=20140730%2F&inqy_sttg_ymdtoday=20190730%2F&inqy_sttg_ymd=2019-01-31&inqy_eymd_yy=2019&inqy_eymd_mm=07&inqy_eymd_dd=30&inqy_eymdDateBoxType=period&inqy_eymdfromday=20140730%2F&inqy_eymdtoday=20190730%2F&inqy_eymd=2019-07-30&_DUMMY_INPUT=&transkeyUuid=b4d0dab4f94fdbf3533b44a9c9e110d6bc49ced177d77b4e9223bd1f05b7ba67&transkey_acnt_pwd=%24a5%2C47%2C13%2Ca4%2C83%2Cfe%2Cd2%2C59%2Cda%2C24%2Cd8%2C9c%2C4a%2C88%2Ccd%2Cec%242b%2Cf7%2Cae%2Cef%2C39%2Cb5%2C2e%2C82%2Cb1%2Ce6%2Ccb%2Cd2%2C34%2Ce7%2C29%2Cf7%2483%2C42%2C76%2C84%2C97%2C6e%2C94%2Cc7%2C62%2Ca7%2C34%2Cd6%2C1e%2C34%2Cf8%2C8f%249e%2Cd7%2Cbb%2C99%2C3f%2C9e%2Cfc%2C90%2C3e%2C85%2C6d%2Ce9%2Ce7%2C22%2C10%2C87%2475%2Cad%2C74%2Cfb%2Cdc%2C66%2Cd9%2C56%2C13%2Cbb%2C80%2C7c%2Ce1%2Cea%2Ce8%2Cc9%2461%2Ce%2C39%2C7a%2C68%2C8a%2Cfd%2C1f%2Cc6%2C20%2C7a%2Cde%2C7%2C9d%2C48%2C58%2433%2C38%2Cc9%2C60%2C9e%2C33%2C39%2C9f%2Cec%2C3b%2Ccb%2C78%2C3b%2C88%2C91%2Ce7&transkey_HM_acnt_pwd=f2da885f046c91ba19eac0491a3e75ba4f7945a8d2a2851f66d956a26272794d&Tk_acnt_pwd_separator=e2e&transkey_rnno=%24a%2C44%2C10%2C19%2C1a%2Ca3%2C31%2C14%2C3a%2C45%2Cca%2Cfd%2C81%2Ce2%2Ccf%2C1b%24be%2C28%2Cb4%2C5%2Cf1%2C5d%2Cbf%2C86%2Cce%2Ced%2C76%2C2f%2C27%2C8%2Ce7%2C40%2446%2C32%2C7a%2C50%2C84%2C83%2C57%2Ce8%2C94%2C7e%2C8b%2Cba%2C71%2Cfb%2C6f%2C8d%2473%2Cd2%2C80%2C3e%2Cb7%2C7e%2Cf3%2Ce4%2C17%2C0%2Ca2%2C5f%2Ceb%2Ca5%2C46%2C91%2475%2Cad%2C74%2Cfb%2Cdc%2C66%2Cd9%2C56%2C13%2Cbb%2C80%2C7c%2Ce1%2Cea%2Ce8%2Cc9%241f%2Ce%2C77%2C6c%2Cb5%2Cfe%2Cc8%2C8e%2Ce8%2Cb3%2C4e%2C9a%2C10%2Cb7%2C57%2Cd9%2475%2Cad%2C74%2Cfb%2Cdc%2C66%2Cd9%2C56%2C13%2Cbb%2C80%2C7c%2Ce1%2Cea%2Ce8%2Cc9%246e%2Cc3%2C80%2C41%2Cee%2Ccf%2C29%2C34%2C67%2C24%2Cc5%2C77%2Cb5%2C0%2C23%2C3d&transkey_HM_rnno=f579e7f2faa69b462a1ff601d88ec96aacf0ff2244a62f337108bb25de4a7b36&Tk_rnno_separator=e2e&hid_key_data=11e0f4bd38e09a47f6fa906dd542e93fe5f59b63447f3bb25dca8fd4505c1f761c678cf5781fe2c501fa34afe7a5352f5e51972cee45cb6f4d1b6dd4075b2392feb7bb4f71b68e79b64f909de9297573c31503023a5c5c0c0a7c4b126a3377c4ffd2af9ada864981d67e6182afdd619c96245161b4127085b133fafd4b9e2ab9ed8cad6e66b9685dd84f58905c83b41dec7259a9567b8ca7323958118847cdebbc657b08d51a4caf498b232e339f2998c388d2b3f6479b4311ce078e230fc5687ad204b0469e6f2f4eb4164a667a21d2ce452ab3613acac5b7dc2de72d66c3bbc6b1edf68d167e5027bd452f4aa2a32dba5cced1cc885c8162e95edf922a8238&hid_enc_data=&E2E_acnt_pwd=9eb3c8342bb6a3e5e0f4a8d7ddec5168915490b934dfe9b5cfe322f9123b68a38afaf1c05cea49016ff4f56f542a386df3e2189c7f79a5f79b6f2496e8b2d632&E2E_rnno=6db4dcf41bd109294c8e83b2fee8164c672a4bf8fdcc647838e3ec124624663ec24a0f1dca1fe65c8edbfa918ab5529ded7df197e1f5ab0ad9d83f4135dbf39e4fe6e0c022ff53a3e71b66e7d95a3c103920fd92ab2c6d59b26d104d77ebb5ec38c806f364cd3ac829803cfdf8e288ab&separate_transkey_gb=e2e&isAjax=true&isGrid=true";

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
				mHttpsConn = (HttpsURLConnection) url.openConnection();
				
//		    	--data 'inq_stdd=20190131&inq_endd=20190730&cus_acn=05414414001019&inq_dcd=1&otpt_trtp_dcd=2&sevr_prn_rqst_cnt=&ipinsideData=%3Dv1IeAGYsjCkAfrpPmZO1MG3ily%2FK9PdiMrfvv3YUHA4dKtwojlmsLeMwWHVixFwVubu%2F35Dzzl%2BxOwat44nqgcFUru3e57mtr5B%2B5hCKHqCXlncVDYhBOf4KyBcSX7Ne6vqxljEsByw2WEbuS6e0pVZu%2FGGyJYQ3Mw135DRpMqf24pSEClKQSFUJrkjvQDn9o3GBY%2FbZz2fdHAG8TnYlkeQPRaFhkEjXoAjgfDOLL77BorB5u8X97y%2FHLHBERhYFx7c9O6ly7oFkuCUrfp8tddOBKrHCjAgee72sU1yywkpDr52kJCq0ULE9SmMhOAl2JGuaBkenccChH7pse%2BG6%2FW1Ign%2BWXhbi%2FpP6ZRAjRv1asC3sRhfB%2BLCaa6E7I3sU7mPBkSJsIxKOdm03%2BwI%2F%2FabYqlrprcNswdVGoYjq25EeUzpbCbev2C8IBxpG%2FvQti%2BiM0I1egTLok6eEvf9eUgE0Vv%2FaCpRZuTEQ64IXPdJH84hIGHKFcp5aE5EUjfjLGukFXAMKQIExK24sGm4t39KDEuhQC8sCGGeCLTT9mDvYCYyuUgVV7C9G5%2BSQ6RF0Tfiu9901y4DiSKxj&ipinsideNAT=xAk2HRloTCj7zhoosUkKabxvj4qETyEf885CeocX67M%2FujwkOSf%2Bpq6vBRNJZtqPqVJ9%2B0bt4i9vwkGt%2BtdsTWkakddrqChNM0XGDDje0YNQfhRL4x337h%2Bq4mtlwDvcZdOOJy2yYYtNGkL3n2Vk5dB4OTGp9KOOp3AeY4kgYsk%3D&ipinsideCOMM=rPz9TJJd0pwzqbwkEMcVoBwR1b4FL24EJBCOobs%2FpJ1X8XO%2Fdbr%2BuUhYNjE8%2B6Amqdu8MLjy%2FrWCO4B6qOTvkSNQUSbYoeup5%2F7qPBG5FoHDkHgIgPNFeex5MDyruhwWPG%2B4b0Jr5mjLBJN1j4VchqMmDqRfhyXgrM2Rpr3g6zsJj0vv9ZxFJEQPRpiw24GJunWnolH2mFJXNCsNIB0krDNfXjD8bjiswxncDeOObPDuDlhXUigdR00xonBwwG02cqD89rmsW%2FnIL0ivgjiuKOler7oj9NxQmVEBZyestydvP9%2FHyuPyxmxW%2B4J%2BpiY365wUx44goV%2FAWuoqQN%2FnbIrkmn2Yj7VlAUdfpUfbl0Hdto24qwVnfrQI%2BrBQ0K6moA9JTJChKAaKLy8ptj%2BCnCqWM9gBJRABkCW5oCSx38%2FuLM5ScyzxKPOKCmcljqcF2GHncZ8V3PjAH1NKdtbw9w%3D%3D&W_SRVR_DATA_DLPR_EN=N&W_SRVR_DATA_DLPR=&pageIndex=1&rebound_yn=N&NEXT_PAGE_TRN_ID_2=&in_cus_acn=05414414001019&acnt_pwd=0000&rnno=0000000&rdo_inq_dcd=1&sel_otpt_trtp_dcd=2&inqy_sttg_ymd_yy=2019&inqy_sttg_ymd_mm=01&inqy_sttg_ymd_dd=31&inqy_sttg_ymdDateBoxType=period&inqy_sttg_ymdfromday=20140730%2F&inqy_sttg_ymdtoday=20190730%2F&inqy_sttg_ymd=2019-01-31&inqy_eymd_yy=2019&inqy_eymd_mm=07&inqy_eymd_dd=30&inqy_eymdDateBoxType=period&inqy_eymdfromday=20140730%2F&inqy_eymdtoday=20190730%2F&inqy_eymd=2019-07-30&_DUMMY_INPUT=&transkeyUuid=6514dbe36fa3fbe41bfb46f326988f4a62ae1d3d7e51abc26bd88bc67a2a85d4&transkey_acnt_pwd=%247c%2Cc0%2C55%2Cbe%2C7d%2Ca8%2Ce2%2C96%2C29%2Ca5%2Cf5%2Ce4%2C22%2Ce4%2C74%2Cc8%24d0%2C7f%2Cc6%2Caa%2Ca3%2Cd4%2C45%2Caa%2Cf6%2C6c%2C77%2Cb5%2C50%2C67%2C88%2C28%248d%2C63%2C93%2Ced%2C67%2Caf%2C81%2Cf4%2Ce0%2C62%2Cdf%2C58%2C38%2C47%2C33%2C40%24f4%2C8a%2Cfb%2Ca2%2C5c%2Ccb%2C3c%2Cb8%2C83%2C2a%2C7f%2C91%2C63%2C6d%2C5%2Cea%248d%2C63%2C93%2Ced%2C67%2Caf%2C81%2Cf4%2Ce0%2C62%2Cdf%2C58%2C38%2C47%2C33%2C40&transkey_HM_acnt_pwd=dadb2ba411ebef6ddb51c75751a98b58e7818b3cd1eb686772927c8e7947d3d5&Tk_acnt_pwd_separator=e2e&transkey_rnno=%24f%2C88%2C9c%2Ccb%2Cf7%2C95%2C35%2C77%2Ca%2Cb%2C3a%2C6c%2C2b%2C74%2Ca%2Cba%24d5%2Ca1%2C6a%2Cb0%2C5%2Cf2%2C45%2Cc9%2Cd1%2C38%2Cbf%2C1e%2C1e%2Cee%2Cd0%2C6b%2441%2C68%2C1a%2Cc%2Cab%2C27%2Ccf%2C39%2C8a%2Cb%2C95%2Cc1%2C29%2C21%2Cd%2C72%241%2C71%2C27%2Ca0%2Ccf%2C26%2Ca9%2C18%2C1a%2Cae%2Ccd%2C50%2C35%2C55%2Cc6%2Cc%24b4%2C7e%2C93%2C41%2Ce9%2C52%2C20%2C89%2C24%2Cd9%2C73%2C29%2Cb2%2C7e%2Cc7%2C44%2462%2Cc%2C66%2C21%2C7b%2C30%2C1c%2C77%2C29%2Cfa%2Cda%2C27%2C72%2Cc6%2C72%2Cd6%248d%2C3e%2Cad%2C7c%2Cef%2C1d%2C41%2Ced%2C88%2Cf0%2C96%2Cca%2C23%2C3a%2Cb5%2C7e%24f6%2Cc2%2C73%2C1c%2Ca1%2C10%2C91%2C3e%2C7b%2Ce%2C53%2Ca%2C48%2C82%2Cf5%2Cc9&transkey_HM_rnno=b89c8343c3e0c0c298f29d30036823679229fbe895ab4898b32b860ba4b0363e&Tk_rnno_separator=e2e&hid_key_data=b59e5642bb03cc722cbfefd0c99fd7f2c189c7bf3e42c1c9683a10944facb91ffcc13024198aa9757786038e30f06016b5fc45b54a3c942c7cfc9b23c788994836394b8d08afefb76fce7bf87359fcb31346cc7ad61b330094d1ad181cc195d5569cc20543d06063f6c94ecfa14c794c295c5154eac5c9b8239a73e23a3110e11f23d92b23ac92cafb573755ca56c731b2976da4180e35576eafe9013f654acfc6a47614b34f4fe370df2998a8d8991e337343366a3eee64cd8caf759fe7820f59f2dfac11d0e3f6695dda1e650bfd426731a2d777510e056f5723767caf76474197253c7d888ebfdaffa087efa34928bdc9b1f49f37ce045431d0ad96b675b1&hid_enc_data=&E2E_acnt_pwd=4f3534daa4faed1ec53fcae746aa7466b8b7cc4010448377ae095b06fd84e090d53b24284ed0b8fb8bdcd063fe8eee273fbef2940a5e568e83fafe50dc7edaf2&E2E_rnno=b1ec478c755d99ad5cf97d7feeacdf8add1ea99138fc36be0ce35f2639a779a8c810c4507f99fb7fa6bd8ec5a6a371651a21f21c441f73e4fe191b37d8f92072562ca6aa3372a39d0df1a3ea7ed0f65f83af955722f5ad432f0eb88fb6a4326dc505c599933b74c798dfccd8308e842a&separate_transkey_gb=e2e&isAjax=true&isGrid=true'
		    	mHttpsConn.setRequestMethod("POST");
				mHttpsConn.addRequestProperty("Accept", "text/html, */*; q=0.01");
				mHttpsConn.addRequestProperty("Request URL", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i4.jsp");
				mHttpsConn.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
				mHttpsConn.addRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,ja;q=0.6");
				mHttpsConn.addRequestProperty("Connection", "keep-alive");
//				mHttpsConn.addRequestProperty("Content-Length", "4312");
				mHttpsConn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				mHttpsConn.addRequestProperty("Cookie", "ccguid=16b1c49760e-314b468060058f098; WMONID=VshF3jta2jK; tabLgnAtmtMvmn=OFRC; _INSIGHT_CK_5602=12436a08d3a840e032ca83ab2bf3e101_47801|e93c9d1c7b85108ae3288ac015003528_39705:1564041554000; _INSIGHT_CK_5603=a968b801af45c01b227f076efec2621e_47705|5b713307d3adf035b12c8baf77891779_66455:1564368255000; ccsession=16c40cb3ee6-327fef0c200cb5033; _INSIGHT_CK_5601=2714700955e8d130e2e4fd6b206293e7_89489|383f2b12e9c771472585f9bd7e71e823_55157:1564456958000; BIGipServer~mybank~pool_mybank_80=713205952.20480.0000; delfino.recentModule=G3; JSESSIONID=0000N0vsov1MMSLvAxmrrzlzTaA:-1");
				mHttpsConn.addRequestProperty("Host", "mybank.ibk.co.kr");
				mHttpsConn.addRequestProperty("Origin", "https://mybank.ibk.co.kr");
				mHttpsConn.addRequestProperty("Sec-Fetch-Mode", "cors");
				mHttpsConn.addRequestProperty("Sec-Fetch-Site", "same-origin");
				mHttpsConn.addRequestProperty("Referer", "https://mybank.ibk.co.kr/uib/jsp/guest/qcs/qcs10/qcs1010/PQCS101000_i.jsp");
				mHttpsConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3860.5 Safari/537.36");
				mHttpsConn.addRequestProperty("X-Requested-With", "XMLHttpRequest");

				if(mHttpsConn.getResponseCode() == 200 && iPage<=1){
					System.out.println(targetUrl);
					
					InputStream is = mHttpsConn.getInputStream();
					byte [] b = new byte[1024];
					int len = 0;
					while( (len = is.read(b, 0, b.length)) != -1){
						tempContentsBuf.append(new String(b,0,len));						
					}

					tempDoc = Jsoup.parse(tempContentsBuf.toString());
					System.out.println(tempDoc);
					
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
