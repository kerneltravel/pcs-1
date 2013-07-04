package com.svo.pcs.model;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSListInfoResponse;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSErrorCode;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.svo.pcs.model.entity.OauthInfo;
import com.svo.pcs.util.Constants;
import com.svo.pcs.util.StringUtil;

public class PcsApi {
	private Context context;
	private String accessToken;
	private Handler uiHandler;
//	private String rootPath = Constants.mbRootPath;
	private BaiduPCSClient api;
	private static PcsApi pcsApi;
	private PcsApi(Context context) {
		this.context = context;
		accessToken = getOauthInfo().getAccessToken();
		api = new BaiduPCSClient();
		if (null != accessToken) {
			api.setAccessToken(accessToken);
		}
		uiHandler = new Handler();
	}
	public static PcsApi getInstance(Context context){
		if (pcsApi == null) {
			pcsApi = new PcsApi(context);
		}
		return pcsApi;
	}
	public void setUiHandler(Handler uiHandler) {
		this.uiHandler = uiHandler;
	}
	public void diff(){
    	if(null != accessToken){
    		final String cursor = context.getSharedPreferences("weitu", 0).getString("diff_cursor", null);
    		Log.i("baidu", "diff:"+cursor);
    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		final BaiduPCSActionInfo.PCSDiffResponse ret = api.diff(cursor);
		    		context.getSharedPreferences("weitu", 0).edit().putString("diff_cursor", ret.cursor).commit();
		    		uiHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(context, "Diff:  " + ret.status.errorCode + "   " + ret.status.message + "  " + ret.entries.size(), Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
    		workThread.start();
    	}
    }
	/**
	 * 创建目录
	 * @param path 目录的绝对路径
	 */
	public PCSFileInfoResponse mkdir(String path){
    	if(null != accessToken){
    		BaiduPCSActionInfo.PCSFileInfoResponse ret = api.makeDir(path);
    		Log.i("baidu", "创建目录："+(ret==null));
    		return ret;
    	}else {
			Log.e("baidu", "没有登录");
		}
		return null;
    }
	/**
	 * 获取某目录下的文件列表
	 */
	public PCSListInfoResponse list(String path){
    	if(null != accessToken){
    		//根据什么来排序，时间，名字
    		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
    		List<PCSCommonFileInfo> list = ret.list;
    		if (list != null && list.size()>0) {
    			for (PCSCommonFileInfo pcsCommonFileInfo : list) {
    				Log.i("baidu", "ret:"+pcsCommonFileInfo.path+";size:"+pcsCommonFileInfo.blockList);
    			}
			}else {
				Log.i("baidu", "list 内容 为 null");
			}
    		return  ret;
    	}
		return null;
    }
	/**
	 * 下载某个文件
	 * @param source 下载文件的绝对路径
	 * @param target 文件的保存路径
	 */
	public void download(final String source,final String target,final BaiduPCSStatusListener listener){
    	if(null != accessToken){
    		Thread workThread = new Thread(new Runnable(){
    			Toast toast;
				public void run() {
		    		final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.downloadFileFromStream(source, target, listener);
				}
			});
    		workThread.start();
    	}
    }
	Toast toast;
	/**
	 * 下载图片
	 * @param source 下载文件的绝对路径
	 * @param target 文件的保存路径
	 */
	public void downloadPic(final String source,final String target,final ImageView imageView){
    	if(null != accessToken){
    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.downloadFileFromStream(source, target, new BaiduPCSStatusListener(){
						@Override
						public void onProgress(long bytes, long total) {
							final long bs = bytes;
							final long tl = total;
							
							uiHandler.post(new Runnable(){
				    			public void run(){
				    				toast = Toast.makeText(context, "total: " + tl + "    downloaded:" + bs, Toast.LENGTH_SHORT);
				    				if (toast != null) {
										toast.cancel();
										toast.show();
									}
				    				if (tl == bs) {
										imageView.setImageURI(Uri.parse(target));
									}
				    			}
				    		});		
						}
						
						@Override
						public long progressInterval(){
							return 500;
						}
		    			
		    		});
		    		
		    		uiHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(context, "Download files:  " + ret.errorCode + "   " + ret.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
	/**
	 * 上传文件
	 * @param filePath 本地上传 文件的路径
	 * @param target 网络文件的绝对路径,包括文件名
	 */
	public void upload(final String filePath,final String target) {
		if (null != accessToken) {
			Thread workThread = new Thread(new Runnable() {
				public void run() {
					String fileName = StringUtil.sepPath(filePath)[1];
					final BaiduPCSActionInfo.PCSFileInfoResponse response = api.uploadFile(filePath, target+"/"+fileName, new BaiduPCSStatusListener() {

						@Override
						public void onProgress(long bytes, long total) {
							final long bs = bytes;
							final long tl = total;
 
							uiHandler.post(new Runnable() {
								public void run() {
									Toast.makeText(context, "total: " + tl + "    sent:" + bs, Toast.LENGTH_SHORT).show();
								}
							});
						}

						@Override
						public long progressInterval() {
							return 1000;
						}
					});

					uiHandler.post(new Runnable() {
						public void run() {
							Toast.makeText(context, response.status.errorCode + "  " + response.status.message + "  " + response.commonFileInfo.blockList, Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
			workThread.start();
		}
	}
	/**
	 * 上传文件
	 * @param filePath 本地上传 文件的路径
	 * @param target 网络文件的绝对路径,包括文件名
	 * @param listener 上传监听
	 */
	public void upload(final String filePath,final String target,BaiduPCSStatusListener listener) {
		if (null != accessToken) {
			String fileName = StringUtil.sepPath(filePath)[1];
			final BaiduPCSActionInfo.PCSFileInfoResponse response = api.uploadFile(filePath, target, listener);
			uiHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(context, response.status.errorCode + "  " + response.status.message + "  " + response.commonFileInfo.blockList, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * 认证
	 */
	public void login(BaiduOAuth.OAuthListener oAuthListener) {
		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(context, Constants.mbApiKey, new String[] { "basic", "netdisk" }, oAuthListener);
	}
	/**
	 * 获得空间的配额信息
	 */
	public void getQuota() {
		if (null != accessToken) {
			Thread workThread = new Thread(new Runnable() {
				public void run() {
					final BaiduPCSActionInfo.PCSQuotaResponse info = api.quota();
					uiHandler.post(new Runnable() {
						public void run() {
							if (null != info) {
								if (0 == info.status.errorCode) {
									Toast.makeText(context, "Quota :" + info.total/1024/1024 + "M  used: " + info.used/1024/1024+"M", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "Quota failed: " + info.status.errorCode + "  " + info.status.message, Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
				}
			});
			workThread.start();
		}
	}

	public void deleteFile(String remotePath) {
		if (null != accessToken) {
				Log.i("baidu", "delete1");
				final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.deleteFile(remotePath);
				uiHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(context, "Delete files:  " + ret.errorCode + "  " + ret.message, Toast.LENGTH_SHORT).show();
					}
				});
		}
	}
	/*
	 * 保存认证信息
	 */
	public void saveAouthInfo(BaiduOAuthResponse response) {
		Editor editor = context.getSharedPreferences("weitu", 0).edit();
		editor.putString("accessToken", response.getAccessToken());
		editor.putString("userName", response.getUserName());
		editor.putString("expiresIn", response.getExpiresIn());
		editor.putString("refreshToken", response.getRefreshToken());
		accessToken = response.getAccessToken();
		api.setAccessToken(accessToken);
		Log.i("baidu", "accessToken:"+accessToken);
		editor.commit();
	}

	/*
	 * 得到认证信息
	 */
	public OauthInfo getOauthInfo() {
		SharedPreferences preferences = context.getSharedPreferences("weitu", 0);
		OauthInfo oauthInfo = new OauthInfo(preferences.getString("accessToken", null), preferences.getString("userName", ""), preferences.getString("expiresIn", ""), preferences.getString("refreshToken", ""));
		return oauthInfo;
	}

	/**
	 * 是否已认证过
	 * 
	 * @return
	 */
	public boolean isOauthed() {
		SharedPreferences preferences = context.getSharedPreferences("weitu", 0);
		String accessToken = preferences.getString("accessToken", "");
		if (TextUtils.isEmpty(accessToken)) {
			return false;
		}
		return true;
	}
	/**
	 * 获得缩略图
	 * @param picPath
	 * @return 
	 */
	public Bitmap thumbnail(String picPath) {
		if (null != accessToken) {
			BaiduPCSActionInfo.PCSThumbnailResponse ret = api.thumbnail(picPath, 100, 90, 90);
			if (BaiduPCSErrorCode.No_Error == ret.status.errorCode) {
				if (null != ret && null != ret.bitmap) {
					return ret.bitmap;
				}
			}
		}
		return null;

	}
}
