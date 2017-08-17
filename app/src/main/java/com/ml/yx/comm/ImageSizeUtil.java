package com.ml.yx.comm;

public class ImageSizeUtil {
	
	private static final String UCLOD_FLAG = "/u-";

	public static String getFeedItemURL(String url) {
		return formatURL(url,200,200);
	}

	public static String getAvatarURL(String url) {
		return formatURL(url,120,120);
	}

	public static String getItemBigImgURL(String url) {
		return formatURL(url,360,240);
	}
	
	public static String formatURL(String url,int width,int height){
		if (StringUtil.isBlank(url) || !url.startsWith("http") || url.contains("?")) {
			return url;
		}
		
		StringBuffer sb = new StringBuffer(url);
		if(url.contains(UCLOD_FLAG)){
			sb.append("?iopcmd=thumbnail&type=8&width=").append(width).append("&height=").append(height);
		}else{
			sb.append("?imageView2/4/w/").append(width).append("/h/").append(height);
		}
		
		return sb.toString();
	}

}
