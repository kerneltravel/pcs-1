package com.svo.pcs.activity;

import java.util.List;
import java.util.Stack;

import yuku.filechooser.FileChooserActivity;
import yuku.filechooser.FileChooserResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.svo.pcs.R;
import com.svo.pcs.model.ImageService;
import com.svo.pcs.model.entity.FileEntity;
import com.svo.pcs.util.Constants;
import com.svo.pcs.util.ErrorMsgUtil;
import com.svo.pcs.util.NetStateUtil;
import com.svo.pcs.util.StringUtil;

public class ImageListActivity extends SherlockActivity {
	private ListView listView;
	private List<FileEntity> entities;
	private ImageService imageService; //service类
	private String curPath = Constants.mbRootPath+Constants.IMAGE; //当前网络路径
	private Stack<String> pathStack = new Stack<String>();
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			items = 1;
    		invalidateOptionsMenu();
			if (msg.what == ImageService.REFRESH_UI) {//来自于ImageService Task
				//转动条消失
				setSupportProgressBarIndeterminateVisibility(false);
				if (msg.arg1 == 1) {//请求成功
					refresh();
				}else if (msg.arg1 == 0) {//请求失败
					Log.e("baidu", "ImageListActivity,errorcode:"+msg.arg2);
					String errMsg = ErrorMsgUtil.getErrorMsg(msg.arg2);
					if (TextUtils.isEmpty(errMsg)) {
						errMsg = "请求失败";
					}
					Toast.makeText(ImageListActivity.this,errMsg, Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == ImageService.REFRESH_ENTITY) {
				refreshEntities();
			}else if (msg.what == ImageService.UPLOAD_FLAG) {
				setSupportProgressBarVisibility(true);
				setSupportProgressBarIndeterminateVisibility(false);
				int value = (int) (msg.arg1*1.0/msg.arg2*10000);
				Log.i("upload", "upload value:"+value);
				setSupportProgress(value);
                if (msg.arg1 == msg.arg2) {
                	setSupportProgressBarVisibility(false);
                	Toast.makeText(getApplicationContext(), "上传完成", Toast.LENGTH_LONG).show();
                	imageService.reqNet(curPath);
				}
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.image_list);
		setSupportProgressBarIndeterminateVisibility(false);
		listView = (ListView) findViewById(R.id.list);
		imageService = new ImageService(this);
		imageService.setHandler(handler);//别忘记设置Handler
		firstRefresh();
		init();
	}
	private void init() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FileEntity entity = entities.get(position);
				if (entity.isDir()) {
					pathStack.push(curPath);
					curPath = entity.getPath();
					firstRefresh();
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				imageService.delReFile(entities.get(arg2).getPath());
				return false;
			}
			
		});
		//初始化列表菜单
//		Context context = getSupportActionBar().getThemedContext();
//	    ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(context, R.array.menu_list, R.layout.sherlock_spinner_item);
//	    listAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
//	    getSupportActionBar().setListNavigationCallbacks(listAdapter, navigationListener);
//	    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	/**
	 * 相当于第一次请求，或者进入一个新目录时的请求
	 */
	private void firstRefresh() {
		entities = imageService.queryBaseRePath(curPath);
		if (entities.size() == 0) { //网络请求,并刷新
			setSupportProgressBarIndeterminateVisibility(true);
			imageService.reqNet(curPath);
		}else {
			listView.setAdapter(new ItemAdapter());
		}
	}
	protected void refreshEntities() {
		entities = imageService.queryBaseRePath(curPath);
	}
	private void refresh() {
		entities = imageService.queryBaseRePath(curPath);
		listView.setAdapter(new ItemAdapter());
	}
	@Override
	public void onBackPressed() {
		if (pathStack.size() > 0) {
			curPath = pathStack.pop();
			refresh();
		}else {
			finish();
		}
	}
	private int items = 1;
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if (items == 1) {
			menu.add("刷新")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		menu.add("上传")
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
	private String fileChoosePath;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	String title = item.getTitle().toString();
    	if ("刷新".equals(title)) {
    		items = 0;
    		invalidateOptionsMenu();
    		setSupportProgressBarIndeterminateVisibility(true);
			imageService.reqNet(curPath);
		}else if ("上传".equals(title)) {
			if (TextUtils.isEmpty(fileChoosePath)) {
				fileChoosePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			}
			imageService.selectFile(ImageListActivity.this,fileChoosePath);
		}
        return true;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	//当前activity销毁时清除多余的线程
    	ImageService.curThreadNum = 0;
    	ImageService.stack.clear();
    	ImageService.threads.clear();
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				FileChooserResult result = FileChooserActivity.obtainResult(data);
				fileChoosePath = result.currentDir;
				String fileName = StringUtil.sepPath(result.firstFilename)[1];
				imageService.upload(result.firstFilename, curPath+"/"+fileName);
				Toast.makeText(getApplicationContext(), "正在上传文件："+fileName, Toast.LENGTH_LONG).show();
//				new AlertDialog.Builder(this)
//				.setTitle("Result")
//				.setMessage(result.currentDir+";"+result.firstFilename)
//				.show();
			}
		}
	}
	class ItemAdapter extends BaseAdapter {
		public int getCount() {
			return entities.size();
		}
		public Object getItem(int position) {
			return position;
		}
		public long getItemId(int position) {
			return position;
		}
		@Override
		public int getItemViewType(int position) {
			return super.getItemViewType(position);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
			TextView text = (TextView) convertView.findViewById(R.id.text);
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			TextView down = (TextView) convertView.findViewById(R.id.down);
			FileEntity entity = entities.get(position);
			text.setText(entity.getFileName());
			if (entity.isDown()) {
				down.setText("打开");
			} else {
				down.setText("下载");
			}
			if (entity.isDir()) {
				image.setImageResource(R.drawable.folder);
				down.setVisibility(View.GONE);
			}else if (Constants.IMAGE.equals(entity.getType())) {
				imageService.disPlayImg(image,entity.getPath());
			}
			down.setOnClickListener(new DownListener(down, entity));
			return convertView;
		}
	}
	class DownListener implements View.OnClickListener{
		TextView btn;
		FileEntity entity;
		public DownListener(TextView btn,FileEntity entity){
			this.btn = btn;
			this.entity = entity;
		}
		@Override
		public void onClick(View v) {
			if ("下载".equals(btn.getText())) {
				if (!NetStateUtil.isNetworkAvailable(getApplicationContext())) {
					Toast.makeText(ImageListActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				btn.setText("下载中");
				imageService.downPic(btn,entity);
			}else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://"+entity.getLocalPath()), "image/*");
				startActivity(intent);
			}
		}
	}
}