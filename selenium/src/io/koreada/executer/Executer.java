package io.koreada.executer;

import java.util.ArrayList;

import io.koreada.parser.HashCodeList;

public abstract class Executer {
	public void closeDriver() {
	};
	public ArrayList<?> operate(int iMode){
		return null;
	}
	public HashCodeList getHashCodeList() {
		return null;
	};
}
