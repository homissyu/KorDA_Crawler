package io.koreada.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class UrlConnection {
	private static UrlConnection INSTANCE = new UrlConnection();
	private URL url = null;
	private URLConnection urlConn= null;

	public static UrlConnection getInstance() {
		return INSTANCE;
	}

	public void connect(String ip, int port){
		try {
			url = new URL("http","eais.saha.go.kr", 80,"/eais/login.do?actionFlag=login");
			urlConn = url.openConnection();
			urlConn.connect();
		}catch(Exception e)  {
			e.printStackTrace();
		}
	}

	public void connect(String ip, int port, String args){
		try {
			url = new URL("http",ip, port, args);
			urlConn = url.openConnection();
			urlConn.connect();
		}catch(Exception e)  {
			e.printStackTrace();
		}
	}

	public URLConnection getUrlConnection(){
		return this.urlConn;
	}

	public String getRealIpAddress(String aUrlName) throws UnknownHostException{
		String ret = null;
		InetAddress ia = InetAddress.getByName(aUrlName);
		ret = ia.getHostAddress();
		return ret;
	}

	public void send() throws IOException{

	}
}
