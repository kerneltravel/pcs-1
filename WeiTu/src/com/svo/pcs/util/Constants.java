package com.svo.pcs.util;

import java.io.File;

import android.content.Context;

public class Constants {
	public final static String mbApiKey = "LGjmiW2GQcSQTGjscsUrbi9c";
	public final static String mbRootPath =  "/apps/weitu/";
	public final static String IMAGE =  "Image";
	public final static String AUDIO =  "Audio";
	public final static String VIDEO =  "Video";
	public final static String DOCUMENT =  "Document";
	public final static String ZIP =  "Zip";
	public static String Local_root;
	public static String thumb_path;
	public void init(Context context) {
		Local_root = context.getExternalCacheDir().getAbsolutePath();
		thumb_path = Local_root+"/thumbnail";
		File dirFile = new File(thumb_path);
		if (null != dirFile && !dirFile.exists()) {
			dirFile.mkdirs();
		}
	}
}
