package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.AppManager;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.bean.VersionInfo;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.service.MessageReceiveService;
import com.yuzhuo.easyupbring.utils.DataCleanManager;
import com.yuzhuo.easyupbring.utils.MyProperUtil;
import com.yuzhuo.easyupbring.utils.UpdateManager;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends BaseAct {

	@InjectView(id = R.id.setting_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.setting_linear_reset_password, click = "onClickCallBack")
	private LinearLayout resetPswLayout;
	@InjectView(id = R.id.setting_linear_binding_phone, click = "onClickCallBack")
	private LinearLayout bindindLayout;
	@InjectView(id = R.id.setting_linear_checkversion, click = "onClickCallBack")
	private LinearLayout checkVersionLayout;
	@InjectView(id = R.id.setting_linear_clear_cache, click = "onClickCallBack")
	private LinearLayout clearCacheLayout;
	@InjectView(id = R.id.setting_txt_exit, click = "onClickCallBack")
	private TextView exitTextView;
	@InjectView(id = R.id.setting_txt_cache_capacity)
	private TextView cachaTextView;
	@InjectView(id = R.id.setting_txt_nowversion)
	private TextView versionTextView;
	@Inject
	private CusPerference cusPerference;
	UpdateManager manager;
	String pathString = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/bianbiangou/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_setting);
		cusPerference.load();
		cachaTextView.setText(DataCleanManager.totalCache(SettingActivity.this,
				pathString));
		versionTextView.setText(AppContext.VERSION);
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.setting_linear_back:
			finish();
			break;
		case R.id.setting_linear_reset_password:

			intent.setClass(SettingActivity.this, ResetPswActivity.class);
			startActivity(intent);
			break;

		case R.id.setting_txt_exit:
			bombDialog();

			break;
			
		case R.id.setting_linear_clear_cache:
			AlertDialog.Builder builder = new Builder(SettingActivity.this);
			builder.setMessage("您确定要清空吗？");
			builder.setPositiveButton("取消",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							DataCleanManager.cleanApplicationData(context,
									pathString);
							Toast.makeText(SettingActivity.this, "清除成功",
									Toast.LENGTH_SHORT).show();
							cachaTextView.setText("0.00B");
						}
					});
			builder.create().show();
			break;
			
		case R.id.setting_linear_checkversion:
			doCheackVersion("0");
			break;

		case R.id.setting_linear_binding_phone:
			intent.setClass(SettingActivity.this, BindindPhoneActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public void bombDialog() {
		AlertDialog.Builder builder1 = new Builder(SettingActivity.this);
		builder1.setMessage("您确定要退出登录吗？");
		builder1.setPositiveButton("取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder1.setNegativeButton("确定",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						AppContext.uid = "";
						cusPerference.uid = "";
						cusPerference.userName = "";
						cusPerference.password = "";
						cusPerference.msg_time = "1447753069856";
						AppContext.userInfoBean = null;
						cusPerference.commit();
						MessageManager.getInstance(SettingActivity.this)
								.deleteData();
						// JPushInterface.setAliasAndTags(SelfInfoActivity.this,
						// null, null);
						AppManager.getAppManager().finishAllActivity();
						Intent intent = new Intent(SettingActivity.this,
								LoginActivity.class);
						startActivity(intent);
						stopMessageService();

					}
				});

		builder1.create().show();
	}

	void doCheackVersion(String platformtype) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platformtype", platformtype);
		String url = MyProperUtil.getProperties(SettingActivity.this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(SettingActivity.this,
						"appConfigDebug.properties").getProperty(
						"getAppVersion");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(SettingActivity.this) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("MJJ", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				Log.e("responseString", response.plain());
				VersionInfo info = response
						.modelFrom(VersionInfo.class, "data");
				manager = new UpdateManager(SettingActivity.this);
				int flag = manager.checkVersion(info);
				if (flag == 3) {
					showNoticeDialog();
				} else {
					showNoticeDialog(flag);
				}
			}
		});
	}

	/**
	 * 弹出更新提示对话框
	 */
	private void showNoticeDialog(final int flag) {
		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("更新提示！");
		if (flag == 1) {
			builder.setMessage("您的版本过低，请下载更新！");
		} else {
			builder.setMessage("您有可更新的版本！");
		}

		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				manager.showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (flag == 1) {
					AppManager.getAppManager().AppExit(context);
					//System.exit(0);
				} else {

				}
			}
		}).create().show();
	}
	
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("更新提示！");
		builder.setMessage("您当前的版本是最新版本！");
		builder.setNegativeButton("确定", null);
		builder.create().show();
	}
	
	public void stopMessageService() {
		Intent serviceIntent = new Intent(this, MessageReceiveService.class);
		stopService(serviceIntent);
	}
}
