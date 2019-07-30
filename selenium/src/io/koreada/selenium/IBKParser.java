package io.koreada.selenium;

import java.util.ArrayList;
import io.koreada.util.CommonConst;

public class IBKParser {
	public ArrayList<AccountInfo> parse(String aVal) {
		ArrayList<AccountInfo> rows = new ArrayList<AccountInfo>();
		String[] arr = aVal.split("\\p{C}");
		AccountInfo accountInfo = null;
		for(int i=0;i<arr.length;i++) {
			if(i%10==0) { 
				accountInfo = new AccountInfo();
				accountInfo.setRegData(arr[i]);
				accountInfo.setWithdraw(arr[i+1]);
				accountInfo.setDeposit(arr[i+2]);
				accountInfo.setBalance(arr[i+3]);
				accountInfo.setContents(arr[i+4]);
				accountInfo.setSrcAccNo(arr[i+5]);
				accountInfo.setSrcBank(arr[i+6]);
				accountInfo.setCMSCode(arr[i+7]);
				accountInfo.setTrType(arr[i+8]);
				accountInfo.setNonPay(arr[i+9]);
//				rows.put(i/10, accountInfo);
				rows.add(accountInfo);
			}
		}
		
		return rows;
	}
	
	class AccountInfo {
    	//순번, 거래일시, 출금금액, 입금금액, 잔액, 거래내용, 상대계좌번호, 상대은행, CMS코드, 거래구분, 미결제(수표/어음) 열로 이루어진 데이터입니다.
    	private String regData = null;
    	private long withdraw = 0;
    	private long deposit = 0;
    	private long balance = 0;
    	private String contents = null;
    	private String srcAccNo = null;
    	private String srcBank = null;
    	private String CMSCode = null;
    	private String trType = null;
    	private long nonPay = 0;
		
    	protected String getRegData() {
			return regData;
		}
		protected void setRegData(String regData) {
			this.regData = regData;
		}
		protected double getWithdraw() {
			return withdraw;
		}
		protected void setWithdraw(String withdraw) {
			this.withdraw = Long.parseLong(withdraw);
		}
		protected double getDeposit() {
			return deposit;
		}
		protected void setDeposit(String deposit) {
			this.deposit = Long.parseLong(deposit);
		}
		protected double getBalance() {
			return balance;
		}
		protected void setBalance(String balance) {
			this.balance = Long.parseLong(balance);
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
		protected double getNonPay() {
			return nonPay;
		}
		protected void setNonPay(String nonPay) {
			this.nonPay = Long.parseLong(nonPay);
		}
    	
    	public String toString(){
    		StringBuffer ret = new StringBuffer();
    		ret.append("(");
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
    		ret.append(")");
    		return ret.toString();
    	}
    }
}
