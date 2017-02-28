package com.xiaoqu.bike.dto;

public class NettyMessage {
	
	private String key;
	
	private byte[] infoBytes;
	
	private int serialNo;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public byte[] getInfoBytes() {
		return infoBytes;
	}
	public void setInfoBytes(byte[] infoBytes) {
		this.infoBytes = infoBytes;
	}
	public int getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}
	
}
