package com.svo.pcs;

import android.app.Application;

import com.svo.pcs.util.Constants;

public class UILApplication extends Application {
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();
		new Constants().init(getApplicationContext());
	}
}