package com.ml.yx.location;

import android.content.Context;

import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.web.JsonToBeanHandler;

import java.io.Serializable;

public class BBLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6594900171061066465L;
	private String cityName;
	private double latitude;
	private double longitude;
	private String cityCode;
	private String district;
	private String address;
	private String province;
	private String street;
	private String streetNumber;

	private static BBLocation mLocation;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public static void saveLocation(Context context,BBLocation location){
		if(location == null){
			return;
		}

		mLocation = location;

		String content = null;
		try {
			content = JsonToBeanHandler.getInstance().toJsonString(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(content != null) {
			SharedPreferencesUtil.putString(BBLocation.class.getSimpleName(), content);
		}
	}

	public static BBLocation getLocation(Context context){
		if(mLocation != null){
			return mLocation;
		}

		String content = SharedPreferencesUtil.getString(BBLocation.class.getSimpleName(),null);
		if(context != null){
			mLocation = JsonToBeanHandler.getInstance().fromJsonString(content,BBLocation.class);
		}
		return mLocation;
	}

}
