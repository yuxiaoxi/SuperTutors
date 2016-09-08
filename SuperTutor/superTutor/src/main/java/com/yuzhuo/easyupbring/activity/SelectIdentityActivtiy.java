package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectIdentityActivtiy extends BaseAct {

	@InjectView(id = R.id.select_identity_txt_parent, click = "onClickCallBack")
	private TextView select_identity_txt_parent;
	@InjectView(id = R.id.select_identity_txt_teacher, click = "onClickCallBack")
	private TextView select_identity_txt_teacher;
	@InjectView(id = R.id.select_identity_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_selectidentity);
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.select_identity_txt_parent:
			//家长
			updateUserInfo(AppContext.username, "0");
			break;

		case R.id.select_identity_txt_teacher:
			//老师
			updateUserInfo(AppContext.username, "1");
			break;

		case R.id.select_identity_linear_back:
			finish();
			break;

		default:
			break;
		}
	}

	public void updateUserInfo(final String uid, final String character) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("character", character);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("updateUserInfo")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(this) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					Intent intent = new Intent();
					intent.setClass(SelectIdentityActivtiy.this,
							SetSelfInfoActivity.class);
					intent.putExtra("character", character);
					intent.putExtra("uid", uid);
					startActivity(intent);
					AppContext.CHARACTER = character;
					Toast.makeText(
							SelectIdentityActivtiy.this,
							"设置成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SelectIdentityActivtiy.this, "失败！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}
}
