package io.koreada.parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.koreada.util.CommonConst;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class AccountInfo {
	//순번, 거래일시, 출금금액, 입금금액, 잔액, 거래내용, 상대계좌번호, 상대은행, CMS코드, 거래구분, 미결제(수표/어음) 열로 이루어진 데이터입니다.
	private int no = 1;
	
	protected int getNo() {
		return no;
	}
	protected void setNo(int no) {
		this.no = no;
	}

	private String regData = null;
	private double withdraw = 0;
	private double deposit = 0;
	private double balance = 0;
	private String contents = null;
	private String srcAccNo = null;
	private String srcBank = null;
	private String CMSCode = null;
	private String trType = null;
	private double nonPay = 0;
	
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
		this.withdraw = Double.parseDouble(withdraw);
	}
	protected double getDeposit() {
		return deposit;
	}
	protected void setDeposit(String deposit) {
		this.deposit = Double.parseDouble(deposit);
	}
	protected double getBalance() {
		return balance;
	}
	protected void setBalance(String balance) {
		this.balance = Double.parseDouble(balance);
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
		this.nonPay = Double.parseDouble(nonPay);
	}
	
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("(");
		ret.append("RegDate="+this.getRegData());
		ret.append(CommonConst.COMMA);
		ret.append("Withdraw="+this.getWithdraw());
		ret.append(CommonConst.COMMA);
		ret.append("Deposit="+this.getDeposit());
		ret.append(CommonConst.COMMA);
		ret.append("Balance"+this.getBalance());
		ret.append(CommonConst.COMMA);
		ret.append("Contents="+this.getContents());
		ret.append(CommonConst.COMMA);
		ret.append("SrcAccountNo="+this.getSrcAccNo());
		ret.append(CommonConst.COMMA);
		ret.append("SrcBank="+this.getSrcBank());
		ret.append(CommonConst.COMMA);
		ret.append("CMSCode"+this.getCMSCode());
		ret.append(CommonConst.COMMA);
		ret.append("TrType"+this.getTrType());
		ret.append(CommonConst.COMMA);
		ret.append("NonPay="+this.getNonPay());
		ret.append(")");
		return ret.toString();
	}
}