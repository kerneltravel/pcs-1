package com.svo.pcs.activity;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.svo.pcs.R;
import com.svo.pcs.model.PcsApi;
import com.svo.pcs.util.Constants;

public class ImageGridActivity extends SherlockActivity {
	private GridView gridView;
	private PcsApi pcsApi;
	private List<PCSCommonFileInfo> list;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			/*if (msg.what == Constants.List) {
				BaiduPCSActionInfo.PCSListInfoResponse ret = (PCSListInfoResponse) msg.obj;
				list = ret.list;
	    		if (list != null && list.size()>0) {
	    			refresh();
	    			for (PCSCommonFileInfo pcsCommonFileInfo : list) {
	    				Log.i("baidu", "ret:"+pcsCommonFileInfo.path+";size:"+pcsCommonFileInfo.size);
	    			}
				}
			}*/
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_grid);
		gridView = (GridView) findViewById(R.id.gridview);
		pcsApi = PcsApi.getInstance(this);
		pcsApi.setUiHandler(handler);
		pcsApi.list(Constants.mbRootPath+"pic/meinv");
	}
	protected void refresh() {
		ImageAdapter adapter = new ImageAdapter();
		gridView.setAdapter(adapter);
	}
	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}
			PCSCommonFileInfo pcsCommonFileInfo = list.get(position);
			Log.i("ImageGridActivity", "pcsCommonFileInfo.path:"+pcsCommonFileInfo.path);
			File dir = new File(Constants.Local_root+"/pic/meinv/");
			if (dir != null && !dir.exists()) {
				dir.mkdirs();
			}
			pcsApi.downloadPic(pcsCommonFileInfo.path, Constants.Local_root+"/pic/meinv/"+pcsCommonFileInfo.path.hashCode(),imageView);
			return imageView;
		}
	}
}
