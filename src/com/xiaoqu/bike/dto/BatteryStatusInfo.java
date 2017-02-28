package com.xiaoqu.bike.dto;

import java.util.Date;

public class BatteryStatusInfo {
    private Integer id;

    private String imei;

    private String longitude;

    private String latitude;

    private Integer batteryLevel;

    private short batteryStatus;

    private Date dataTime;
    public BatteryStatusInfo(){
    	
    }
    public BatteryStatusInfo( String imei,
			String longitude, String latitude, Integer batteryLevel,
			short batteryStatus, Date dataTime) {
		
		this.imei = imei;
		this.longitude = longitude;
		this.latitude = latitude;
		this.batteryLevel = batteryLevel;
		this.batteryStatus = batteryStatus;
		this.dataTime = dataTime;
	}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public short getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(Byte batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }
}