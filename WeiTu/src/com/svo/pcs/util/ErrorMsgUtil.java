package com.svo.pcs.util;

import java.util.HashMap;

public class ErrorMsgUtil {
	public static HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
	static{
		hashMap.put(4, "您没有权限执行此操作");
		hashMap.put(5, "IP未授权");
		hashMap.put(31021, "网络错误");
		hashMap.put(31022, "暂时无法连接服务器");
		hashMap.put(31025, "后端存储错误");
		hashMap.put(31042, "用户未登陆");
		hashMap.put(31043, "用户未激活");
		hashMap.put(31044, "用户未授权");
		hashMap.put(31061, "文件已经存在");
		hashMap.put(31062, "文件名非法");
		hashMap.put(31063, "文件父目录不存在");
		hashMap.put(31064, "无权访问此文件");
		hashMap.put(31066, "文件不存在");
		hashMap.put(31067, "文件处理出错");
		hashMap.put(31068, "文件创建失败");
		hashMap.put(31069, "文件拷贝失败");
		hashMap.put(31070, "文件删除失败");
		hashMap.put(110, "您需要重新登录");
		hashMap.put(31202, "文件不存在");
		hashMap.put(31212, "服务不可用");
		hashMap.put(31220, "流量超出限额");
		hashMap.put(31214, "上传文件失败");
		hashMap.put(31215, "上传文件失败");
		hashMap.put(31216, "下载文件失败");
		hashMap.put(31217, "文件处理出错");
	}
	public static String getErrorMsg(int errorCode) {
		return hashMap.get(errorCode);
	}
}
