package com.svo.pcs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.svo.pcs.MainActivity;
import com.svo.pcs.R;
import com.svo.pcs.model.PcsApi;

public class Welcome extends SherlockActivity {
	PcsApi api;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		api = PcsApi.getInstance(this);
		if (api.isOauthed()) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
	public void login(View view) {
		api.login(new BaiduOAuth.OAuthListener() {
			@Override
			public void onException(String msg) {
				Toast.makeText(Welcome.this, "Login failed " + msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(BaiduOAuthResponse response) {
				if (null != response) {
					api.saveAouthInfo(response);
					Toast.makeText(Welcome.this, "User name:" + response.getUserName(), Toast.LENGTH_SHORT).show();
					startActivity(new Intent(Welcome.this, MainActivity.class));
					finish();
				}
			}

			@Override
			public void onCancel() {
				Toast.makeText(Welcome.this, "Login cancelled", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
