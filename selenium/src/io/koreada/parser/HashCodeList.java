package io.koreada.parser;

import java.util.ArrayList;

public class HashCodeList implements java.io.Serializable {
	private static final long serialVersionUID = -2518143648593030L;
	private ArrayList<String> mHashcodeList = new ArrayList<String>();
	public ArrayList<String> getHashCodeList() {
		return mHashcodeList;
	}
	public void appendHashCode(String aVal) {
		mHashcodeList.add(aVal);
	}
	
	public void clear() {
		mHashcodeList.clear();
	}
	
	public boolean equals(HashCodeList aVal) {
		boolean ret = false;
		if(this.mHashcodeList.equals(aVal.mHashcodeList)) ret = true;
		return ret;
	}
	public void addAll(HashCodeList aVal) {
		// TODO Auto-generated method stub
		this.mHashcodeList.addAll(aVal.mHashcodeList);
	}
	
	public boolean containsAll(HashCodeList aVal) {
		return this.mHashcodeList.containsAll(aVal.mHashcodeList);
	}
	
	public void removeAll(HashCodeList aVal) {
		this.mHashcodeList.removeAll(aVal.mHashcodeList);
	}
	
	public boolean contains(String aHashCode) {
		return this.mHashcodeList.contains(aHashCode);
	}
}