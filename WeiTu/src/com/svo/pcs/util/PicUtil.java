package com.svo.pcs.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class PicUtil {
	/**
	 * 将Bitmap保存为图片
	 * @param path 保存路径
	 * @param bitmap 
	 */
	public static void saveBm(final String path, final Bitmap bitmap) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(Constants.thumb_path+"/"+path.hashCode());
			bitmap.compress(CompressFormat.PNG, 100, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
