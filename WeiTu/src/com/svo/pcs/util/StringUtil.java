package com.svo.pcs.util;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;
import android.util.Log;


/**
 * 字符串操作工具类
 * @author duweibin 
 * @version 创建时间：2012-10-29 上午10:05:39
 */
public class StringUtil {
	
	private static final String TAG = "StringUtil";

    
    /**
     * 根据一个文件的绝对路径得到文件所在的目录路径和文件名
     * @param filePath 某文件的绝对路径
     * @return 长度为2的数组,第一个元素是目录路径,带斜杠.第二个元素是文件名.可能为null
     */
    public  static String[] sepPath(String filePath) {
    	if (filePath == null || !filePath.contains("/") || (filePath.lastIndexOf("/") == (filePath.length() - 1))) {
    		Log.e(TAG, "不是合法的文件路径");
			return null;
		}
    	int index = filePath.lastIndexOf("/")+1;
    	String[] arr = new String[2];
    	arr[0] = filePath.substring(0,index);
    	arr[1] = filePath.substring(index);
    	return arr;
	}
    
	
	/**
	 * 将字符串数组转换为以逗号分隔的字符串
	 * @param strArr 字符串数组
	 * @return String
	 */
	public static String strArr2str(String[] strArr) {
		StringBuilder sb = new StringBuilder();
		for (String str : strArr) {
			sb.append(","+str);
		}
		return sb.toString();
	}
	/**
	 * 得到文件的后缀名
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName) {
		if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}
		return "";
	}
}
