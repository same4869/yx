package com.ml.yx.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WenbaLocationClient implements BDLocationListener {
	private static final String tag = WenbaLocationClient.class.getSimpleName();

	private Context mContext;
	
	private LocationClient mLocationClient = null;
	
	private Queue<WenbaLocationCallback> callbackQueue;

	public interface WenbaLocationCallback {
		public void locationCallback(BBLocation bbLocation);
	}

	public WenbaLocationClient(Context context) {
		mLocationClient = new LocationClient(context.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(this); // 注册监听函数
		
		callbackQueue = new ConcurrentLinkedQueue<WenbaLocationCallback>();//初始化一个线程安全的队列

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setOpenGps(true);
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000 * 60 * 10);// 设置发起定位请求的间隔时间
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		mLocationClient.setLocOption(option);
	}

	public void requestLocation(WenbaLocationCallback wenbaLocationCallback) {
		//BBLog call replaced
		if(wenbaLocationCallback != null){
			callbackQueue.add(wenbaLocationCallback);//将回调对象添加到队列中
		}
		
		startLoc();
		mLocationClient.requestLocation();
	}

	private void startLoc() {
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
	}

	public void stopLoc() {
		if (mLocationClient.isStarted() && callbackQueue.isEmpty()) {
			mLocationClient.stop();
		}
	}

	@Override
	public void onReceiveLocation(BDLocation dbLocation) {
		BBLocation bbLocation = null;
		if(dbLocation != null){
			int locType = dbLocation.getLocType();
			//BBLog call replaced
			if (locType == 61 || locType == 161) {
				if (Constants.IS_DEBUG) {
					APPUtil.showToast(mContext, "定位成功：" + locType);
				}
				bbLocation = new BBLocation();
				if (dbLocation.getProvince() != null && dbLocation.getProvince().contains("市")) {
					bbLocation.setProvince(dbLocation.getProvince().replace("市", ""));
					if (dbLocation.getCity() != null) {
						bbLocation.setCityName(dbLocation.getCity().replace("市", ""));
					}
				} else {
					bbLocation.setProvince(dbLocation.getProvince());
					bbLocation.setCityName(dbLocation.getCity());
				}
				bbLocation.setAddress(dbLocation.getAddrStr());
				bbLocation.setCityCode(dbLocation.getCityCode());
				bbLocation.setDistrict(dbLocation.getDistrict());
				bbLocation.setLatitude(dbLocation.getLatitude());
				bbLocation.setLongitude(dbLocation.getLongitude());
	
				bbLocation.setStreet(dbLocation.getStreet());
				bbLocation.setStreetNumber(dbLocation.getStreetNumber());

				BBLocation.saveLocation(mContext,bbLocation);// 保存地理位置信息
			} else {
				if (Constants.IS_DEBUG) {
					APPUtil.showToast(mContext, "定位失败：" + locType);
				}
			}
		}
		
		//BBLog call replaced
		
		if(!callbackQueue.isEmpty()){
			if(bbLocation == null){
				bbLocation = BBLocation.getLocation(mContext);
			}
			while(!callbackQueue.isEmpty()){
				WenbaLocationCallback wenbaLocationCallback = callbackQueue.poll();//获得头部对象并移除头部对象
				if(wenbaLocationCallback != null){
					wenbaLocationCallback.locationCallback(bbLocation);
				}
			}
		}
		
		//BBLog call replaced
		
		stopLoc();
	}
}
