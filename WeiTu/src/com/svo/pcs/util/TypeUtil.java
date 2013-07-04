package com.svo.pcs.util;

public class TypeUtil {
	private final static String imgType = "jpg,jpeg,png,gif,tif,bmp,ico,pcx,tga";
	private final static String audioType = "mp3,wav,amr,wma,ogg,mp2,m4a,m4r";
	private final static String videoType = "mp4,avi,3gp,rmvb,rm,wmv,mkv,mpg,mpeg,vob,flv,swf,mov";
	private final static String docType = "txt,doc,pdf,wps,xls,docx,htm,html,fb2,epub,xml,ppt,mobi";
	private final static String zipType = "zip,rar,7z,tar,iso,gz,gzip,jar,apk,bz";
	/**
	 * 根据热后缀名得到文件类型
	 * @param suffix
	 * @return
	 */
	public static String getType(String suffix) {
		String s = suffix.toLowerCase();
		if (imgType.contains(s)) {
			return Constants.IMAGE;
		} else if (audioType.contains(s)) {
			return Constants.AUDIO;
		} else if (videoType.contains(s)) {
			return Constants.VIDEO;
		} else if (docType.contains(s)) {
			return Constants.DOCUMENT;
		} else if (zipType.contains(s)) {
			return Constants.ZIP;
		} else {
			return "unkown";
		}
	}
}
