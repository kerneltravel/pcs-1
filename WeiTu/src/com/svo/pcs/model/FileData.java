package com.svo.pcs.model;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.svo.pcs.model.dao.DBHelper;
import com.svo.pcs.model.entity.FileEntity;
import com.svo.pcs.util.StringUtil;
import com.svo.pcs.util.TypeUtil;

public class FileData {
	private DBHelper dbHelper;
	private Context context;

	public FileData(Context context) {
		this.context = context;
		dbHelper = DBHelper.getInstance(context);
	}
	/**
	 * 从File表中查询多条记录
	 * @param sql 
	 * @param selectionArgs
	 * @return
	 */
	public List<FileEntity> queryFiles(String sql,String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<FileEntity> entities = new LinkedList<FileEntity>();
		if (cursor == null) {
			return entities;
		}
		try {
			FileEntity entity = null;
			while (cursor.moveToNext()) {
				entity = new FileEntity();
				entity.set_id(cursor.getString(cursor.getColumnIndex("_id")));
				entity.setBlockList(cursor.getString(cursor.getColumnIndex("blockList")));
				entity.setCtime(cursor.getLong(cursor.getColumnIndex("ctime")));
				entity.setDir("yes".equals(cursor.getString(cursor.getColumnIndex("isDir"))) ? true:false);
				entity.setDown("yes".equals(cursor.getString(cursor.getColumnIndex("isDown"))) ? true:false);
				entity.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
				entity.setFs_id(cursor.getString(cursor.getColumnIndex("fs_id")));
				entity.setHasSubFolder("yes".equals(cursor.getString(cursor.getColumnIndex("hasSubFolder"))) ? true:false);
				entity.setLocalPath(cursor.getString(cursor.getColumnIndex("localPath")));
				entity.setMtime(cursor.getLong(cursor.getColumnIndex("mtime")));
				entity.setParDir(cursor.getString(cursor.getColumnIndex("parDir")));
				entity.setPath(cursor.getString(cursor.getColumnIndex("path")));
				entity.setSize(cursor.getLong(cursor.getColumnIndex("size")));
				entity.setSuffix(cursor.getString(cursor.getColumnIndex("suffix")));
				entity.setType(cursor.getString(cursor.getColumnIndex("type")));
				if (entity.isDir()) {
					entities.add(0,entity);
				} else {
					entities.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
			}
		}
		return entities;
	}
	/**
	 * 向file表中插入多条数据
	 * @param list 
	 */
	public void insert(List<PCSCommonFileInfo> list) {
		for (PCSCommonFileInfo pcsCommonFileInfo : list) {
			insert(pcsCommonFileInfo);
		}
	}
	/**
	 * 向file表中插入单条数据
	 * @param fileInfo
	 * @return
	 */
	public long insert(PCSCommonFileInfo fileInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = getFileInfoValue(fileInfo);
		return db.insert("file", "blockList", values);
	}
	/**
	 * 根据PCSCommonFileInfo组织插入数据
	 * @param fileInfo
	 * @return
	 */
	private ContentValues getFileInfoValue(PCSCommonFileInfo fileInfo) {
		ContentValues values = new ContentValues();
		values.put("fs_id", fileInfo.fsId);
		values.put("path", fileInfo.path);
		String[] ss = StringUtil.sepPath(fileInfo.path);
		String dir = ss[0]; //网络路径
		String fileName = ss[1];//文件名
		values.put("parDir", dir);//带斜杠
		values.put("fileName", fileName);
		values.put("ctime", fileInfo.cTime);
		values.put("mtime", fileInfo.mTime);
		values.put("size", fileInfo.size);
		values.put("blockList", fileInfo.blockList);
		values.put("hasSubFolder", fileInfo.hasSubFolder?"yes":"no");
		values.put("isDir", fileInfo.isDir?"yes":"no");
		String suffix = StringUtil.getSuffix(fileName);
		values.put("suffix", suffix);
		values.put("type", TypeUtil.getType(suffix));
		return values;
	}
	public int update(FileEntity entity) {
		ContentValues values = new ContentValues();
		values.put("localPath", entity.getLocalPath());
		values.put("isDown", "yes");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.update("file", values, "_id = ?", new String[]{entity.get_id()});
	}
	/**
	 * 删除某个远程目录下的文件信息
	 * @param rePath 远程文件目录
	 */
	public void delOldFile(String rePath) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("file", "parDir in(?,?)", new String[]{rePath,rePath+"/"});
	}
}
