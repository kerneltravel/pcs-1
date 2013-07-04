package com.svo.pcs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.svo.pcs.activity.ImageListActivity;
import com.svo.pcs.model.HomeService;

public class Home extends SherlockActivity {
	private HomeService homeService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		homeService = new HomeService(this);
		homeService.initDir();
	}

	public void click(View view) {
		int viewId = view.getId();
		switch (viewId) {
		case R.id.pic:
			startActivity(new Intent(this, ImageListActivity.class));
			break;
		case R.id.video:

			break;
		case R.id.audio:

			break;
		case R.id.doc:

			break;
		default:
			break;
		}
	}
}
