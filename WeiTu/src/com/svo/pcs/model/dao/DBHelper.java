package com.svo.pcs.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final int version = 1;
	private static  DBHelper DB_HELPER;
	public DBHelper(Context context) {
		super(context, "weitu.db", null, version);
	}
	public static DBHelper getInstance(Context context) {
		if (DB_HELPER == null) {
			DB_HELPER = new DBHelper(context);
		}
		return DB_HELPER;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE file(_id INTEGER PRIMARY KEY AUTOINCREMENT,fs_id Text,path Text unique,parDir Text,fileName Text,ctime Integer,"
				+ "mtime Integer,size INTEGER,blockList Text,hasSubFolder Text,isDir Text,type Text,suffix Text,localPath Text,isDown Text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
