package com.yuzhuo.easyupbring.activity;


import com.yuzhuo.easyupbring.app.AppManager;
import com.yuzhuo.easyupbring.widget.CustomProgressDialog;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import net.duohuo.dhroid.activity.BaseActivity;

/**
 * 
* @ClassName: BaseAct 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 上午11:36:22 
*
 */
public class BaseAct extends BaseActivity {
	private CustomProgressDialog progressDialog = null;
	public Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.e("activity", getRunningActivityName());
		context = getApplicationContext();
		AppManager.getAppManager().addActivity(this);
	}
	public void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("");
		}

		progressDialog.show();
	}

	public void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	private String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}
	
	@Override
	protected void onDestroy() {
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}
	
}
