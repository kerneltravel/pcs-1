package com.svo.pcs.model;

import java.io.File;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import yuku.filechooser.FileChooserActivity;
import yuku.filechooser.FileChooserConfig;
import yuku.filechooser.FileChooserConfig.Mode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo.PCSListInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.svo.pcs.model.entity.FileEntity;
import com.svo.pcs.util.Constants;
import com.svo.pcs.util.PicUtil;
import com.svo.pcs.util.StringUtil;

public class ImageService {
	private Context context;
	private PcsApi pcsApi; 
//	private String reRootPath = Constants.mbRootPath;
	static boolean isEnd = true;//判断线程有没有结束
	private Handler handler;
	public static int curThreadNum = 0; //当前线程数，用来控制线程数
	public static Hashtable<String, Thread> threads = new Hashtable<String, Thread>(); //当前线程数，用来控制线程数
	public static Stack<String> stack = new Stack<String>();//线程栈
	private FileData fileData;
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public ImageService(Context context) {
		this.context = context;
		pcsApi = PcsApi.getInstance(context);
		fileData = new FileData(context);
	}
	/**
	 * 从File表中查询多条记录
	 * @param sql 
	 * @param selectionArgs
	 * @return
	 */
	public List<FileEntity> queryBaseRePath(String path) {
		String sql = "select * from file where parDir in (?,?) order by mtime";
		return fileData.queryFiles(sql, new String[]{path,path+"/"});
	}
	private class ImageTask extends AsyncTask<String, Void, Integer>{
		PCSListInfoResponse response;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isEnd = false;
		}
		@Override
		protected Integer doInBackground(String... params) {
			if (TextUtils.isEmpty(params[0])) {
				return 2;
			}
			response = pcsApi.list(params[0]);
			if (null == response || null == response.status) {
				return 2;
			}
			if (response.status.errorCode == 0 || response.status.errorCode == 31063 || response.status.errorCode == 31066) {
				if (response.status.errorCode == 0 && response.list.size() > 0) {
					//插入前先将该目录下的旧信息删除 
					fileData.delOldFile(params[0]);
					fileData.insert(response.list);
				}
				return 0;
			}
			return 1;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			isEnd = true;
			Message msg = handler.obtainMessage();
			msg.what = REFRESH_UI;
			if (result==0) {
				msg.arg1 = 1;//表示请求成功还是失败
			} else if(result==1){
				msg.arg1 = 0;
				msg.arg2 = response.status.errorCode;//错误码
			}else if(result==2){
				return;
			}
			handler.sendMessage(msg);
		}
	}
	/**
	 * 请求网络内容
	 * @param curPath 
	 */
	public void reqNet(String curPath) {
		if (isEnd) {
			new ImageTask().execute(curPath);
		}
	}
	public void disPlayImg(final ImageView image, final String path) {
		File file = new File(Constants.thumb_path+"/"+path.hashCode());
		if (null != file && file.exists()) {
			image.setImageURI(Uri.fromFile(file));
			return ;
		}
		Thread thread = new Thread(){
			public void run() {
				final Bitmap bitmap = pcsApi.thumbnail(path);
				if (null != bitmap) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							image.setImageBitmap(bitmap);
						}
					});
					PicUtil.saveBm(path, bitmap);
				}
				--curThreadNum;
				if (threads.size() > 0 && stack.size() > 0) {
					String threadKey = stack.pop();
					Thread  tmpThread = threads.remove(threadKey);
					if (null != tmpThread) {
						tmpThread.start();
						++curThreadNum;
					}
				}
			}
		};
		if (curThreadNum < 4) {
			thread.start();
			++curThreadNum;
		}else {
			threads.put(path, thread);
			stack.add(path);
		}
	}
	
	/**
	 * 下载图片
	 * @param btn 下载按钮
	 * @param entity 下载对象实体
	 */
	public void downPic(final TextView btn, final FileEntity entity) {
		File dir = new File(Constants.Local_root+entity.getParDir());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		final String targetPath = dir.getAbsolutePath()+entity.getFileName();
		pcsApi.download(entity.getPath(), targetPath,new BaiduPCSStatusListener(){
			@Override
			public void onProgress(long bytes, long total) {
				final long bs = bytes;
				final long tl = total;
				if (bs == tl) {
					entity.setLocalPath(targetPath);
					new FileData(context).update(entity);
					handler.sendEmptyMessage(REFRESH_ENTITY);//刷新实体
					handler.post(new Runnable(){
						public void run(){
							btn.setText("打开");
						}
					});
				}else {
					handler.post(new Runnable(){
						public void run(){
							// 获取格式化对象
							NumberFormat nt = NumberFormat.getPercentInstance();
							// 设置百分数精确度2即保留两位小数
							nt.setMinimumFractionDigits(2);
							btn.setText(nt.format(bs * 1.0 / tl));
						}
					});
				}
						
			}
		});
	}
	/**
	 * 选择上传文件
	 * @param activity
	 * @param path
	 */
	public void selectFile(Activity activity, String path) {
		FileChooserConfig config = new FileChooserConfig();
		config.mode = Mode.Open;
		config.initialDir = path;
		config.title = "选择图片";
		config.subtitle = "";
		config.pattern = ".*\\.(?i:jpg|jpeg|png|bmp|gif)";
		activity.startActivityForResult(FileChooserActivity.createIntent(activity.getApplicationContext(), config), 1);
	}
	public static final int UPLOAD_FLAG = 11;
	public static final int REFRESH_ENTITY = 2;
	public static final int REFRESH_UI = 1;
	/**
	 * 上传文件
	 * @param filePath 本地上传 文件的路径
	 * @param target 网络文件的绝对路径,包括文件名
	 */
	public void upload(final String filePath,final String target) {
		new Thread(){
			public void run() {
				pcsApi.upload(filePath, target, new BaiduPCSStatusListener() {
					
					@Override
					public void onProgress(long arg0, long arg1) {
						Message msg = handler.obtainMessage();
						msg.what = UPLOAD_FLAG;
						msg.arg1 = (int) arg0;
						msg.arg2 = (int) arg1;
						handler.sendMessage(msg);
					}
					
					@Override
					public long progressInterval() {
						return 500;
					}
				});
			}
		}.start();
		
	}
	public void delReFile(final String path) {
		new Thread(){
			public void run() {
				pcsApi.deleteFile(path);
			}
		}.start();
	}
}
