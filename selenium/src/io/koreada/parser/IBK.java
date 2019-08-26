package io.koreada.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class IBK {
	private ArrayList<Integer> mHashcodeList = new ArrayList<Integer>();
	
	public HashMap<Integer, AccountInfo> parse(String aVal) throws JsonGenerationException, JsonMappingException, IOException {
		HashMap<Integer, AccountInfo> rows = new HashMap<Integer, AccountInfo>();
		String[] arr = aVal.split("\\p{C}");
		AccountInfo accountInfo = null;
		for(int i=0;i<arr.length;i++) {
			if(i%10==0) { 
				accountInfo = new AccountInfo();
				accountInfo.setNo(i/10);
				accountInfo.setRegData(arr[i]);
//				accountInfo.setWithdraw(arr[i+1]);
				accountInfo.setDeposit(arr[i+2]);
//				accountInfo.setBalance(arr[i+3]);
				accountInfo.setContents(arr[i+4]);
				accountInfo.setSrcAccNo(arr[i+5]);
				accountInfo.setSrcBank(arr[i+6]);
				accountInfo.setCMSCode(arr[i+7]);
				accountInfo.setTrType(arr[i+8]);
				accountInfo.setNonPay(arr[i+9]);
				accountInfo.setHashCode();
				rows.put(accountInfo.getHashCode(), accountInfo);
			}
		}
		return rows;
	}
	
	public ArrayList<Integer> getHashCode() {
		return mHashcodeList;
	}
}
