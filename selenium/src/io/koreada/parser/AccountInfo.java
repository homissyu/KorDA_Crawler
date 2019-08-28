package io.koreada.parser;

import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.koreada.util.CommonConst;
import io.koreada.util.CryptoUtils;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class AccountInfo {
	//占쎈떄甕곤옙, 椰꾧퀡�삋占쎌뵬占쎈뻻, �빊�뮄�닊疫뀀뜆釉�, 占쎌뿯疫뀀뜃�닊占쎈만, 占쎌삆占쎈만, 椰꾧퀡�삋占쎄땀占쎌뒠, 占쎄맒占쏙옙�④쑴伊뽬린�뜇�깈, 占쎄맒占쏙옙占쏙옙占쎈뻬, CMS�굜遺얜굡, 椰꾧퀡�삋�뤃�됲뀋, 沃섎㈇猿먲옙�젫(占쎈땾占쎈ご/占쎈선占쎌벉) 占쎈였嚥∽옙 占쎌뵠�뙴�뫁堉깍쭪占� 占쎈쑓占쎌뵠占쎄숲占쎌뿯占쎈빍占쎈뼄.
	private int no = 1;
	
	protected int getNo() {
		return no;
	}
	protected void setNo(int no) {
		this.no = no;
	}

	private String regData = null;
//	private double withdraw = 0;
	private double deposit = 0;
//	private double balance = 0;
	private String contents = null;
	private String srcAccNo = null;
	private String srcBank = null;
	private String CMSCode = null;
	private String trType = null;
	private double nonPay = 0;
	private String hashCode = null;
	
	protected String getRegData() {
		return regData;
	}
	protected void setRegData(String regData) {
		this.regData = regData;
	}
//	protected double getWithdraw() {
//		return withdraw;
//	}
//	protected void setWithdraw(String withdraw) {
//		this.withdraw = Double.parseDouble(withdraw);
//	}
	protected double getDeposit() {
		return deposit;
	}
	protected void setDeposit(String deposit) {
		this.deposit = Double.parseDouble(deposit);
	}
//	protected double getBalance() {
//		return balance;
//	}
//	protected void setBalance(String balance) {
//		this.balance = Double.parseDouble(balance);
//	}
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
		ret.append("No="+this.getNo());
		ret.append(CommonConst.COMMA);
		ret.append("RegDate="+this.getRegData());
//		ret.append(CommonConst.COMMA);
//		ret.append("Withdraw="+this.getWithdraw());
		ret.append(CommonConst.COMMA);
		ret.append("Deposit="+this.getDeposit());
//		ret.append(CommonConst.COMMA);
//		ret.append("Balance"+this.getBalance());
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
		ret.append(CommonConst.COMMA);
		ret.append("HashCode="+this.getHashCode());
		ret.append(")");
		return ret.toString();
	}
	
	protected String getHashCode() {
		return this.hashCode;
	}
	
	protected void setHashCode() throws NoSuchAlgorithmException {
		StringBuffer ret = new StringBuffer();
		ret.append("(");
		ret.append("No="+this.getNo());
		ret.append(CommonConst.COMMA);
		ret.append("RegDate="+this.getRegData());
//		ret.append(CommonConst.COMMA);
//		ret.append("Withdraw="+this.getWithdraw());
		ret.append(CommonConst.COMMA);
		ret.append("Deposit="+this.getDeposit());
//		ret.append(CommonConst.COMMA);
//		ret.append("Balance"+this.getBalance());
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
		this.hashCode = CryptoUtils.generateSHA256(ret.toString());
	}
}