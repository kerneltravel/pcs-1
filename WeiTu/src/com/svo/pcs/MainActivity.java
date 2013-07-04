package com.svo.pcs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.svo.pcs.activity.ImageGridActivity;
import com.svo.pcs.model.PcsApi;
import com.svo.pcs.util.Constants;

public class MainActivity extends SherlockActivity {

	private PcsApi pcsApi;
	private String rootPath = Constants.mbRootPath;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pcsApi = PcsApi.getInstance(this);
	}
	public void click(View view) {
		int viewId = view.getId();
		switch (viewId) {
		case R.id.audio://login
//			pcsApi.login();
			startActivity(new Intent(this, Home.class));
			break;
		case R.id.video://upload
			String filePath = ((EditText)findViewById(R.id.editText1)).getText().toString();
			String rePath = ((EditText)findViewById(R.id.editText2)).getText().toString();
			pcsApi.upload(filePath,rootPath+rePath);
			break;
		case R.id.doc:
			String remPath = ((EditText)findViewById(R.id.editText1)).getText().toString();
			String localPath = ((EditText)findViewById(R.id.editText2)).getText().toString();
			pcsApi.download(rootPath+remPath,localPath,new BaiduPCSStatusListener() {
				
				@Override
				public void onProgress(long arg0, long arg1) {
					
				}
			});
			break;
		case R.id.button4:
			String remotePath = ((EditText)findViewById(R.id.editText1)).getText().toString();
			pcsApi.list(rootPath+remotePath);
			break;
		case R.id.button5:
			String dirName = ((EditText)findViewById(R.id.editText2)).getText().toString();
			pcsApi.mkdir(rootPath+dirName);
			break;
		case R.id.quata:
			pcsApi.getQuota();
			break;
		case R.id.delete:
			String delPath = ((EditText)findViewById(R.id.editText1)).getText().toString();
			pcsApi.deleteFile(rootPath+delPath);
			break;
		case R.id.test_pic:
//			startActivity(new Intent(this, ImageGridActivity.class));
			startActivity(new Intent(this, Home.class));
			break;
		case R.id.diff:
			pcsApi.diff();
			break;
		default:
			break;
		}
	}
}
