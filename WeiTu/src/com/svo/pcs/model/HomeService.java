package com.svo.pcs.model;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.svo.pcs.util.Constants;

import android.content.Context;
import android.util.Log;

public class HomeService {
	private Context context;
	private PcsApi pcsApi;
	public HomeService(Context context){
		this.context = context;
		pcsApi = PcsApi.getInstance(context);
	}
	/**
	 * 初始化目录 
	 */
	public void initDir() {
		boolean flag = context.getSharedPreferences("weitu", 0).getBoolean("isInited", false);
		if (flag) {
			return;
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.i("baidu", "去创建目录");
					PCSFileInfoResponse response;
					response = pcsApi.mkdir(Constants.mbRootPath+Constants.IMAGE);
					response = pcsApi.mkdir(Constants.mbRootPath+Constants.AUDIO);
					response = pcsApi.mkdir(Constants.mbRootPath+Constants.VIDEO);
					response = pcsApi.mkdir(Constants.mbRootPath+Constants.DOCUMENT);
					if (null != response && null != response.status) {
						Log.i("baidu", "HomeService,去创建目录:"+response.status.errorCode);
						if (response.status.errorCode == 0 || response.status.errorCode == 31061) {
							context.getSharedPreferences("weitu", 0).edit().putBoolean("isInited", true).commit();
						}
					}
				}
			}).start();
		}
	}
	
}
