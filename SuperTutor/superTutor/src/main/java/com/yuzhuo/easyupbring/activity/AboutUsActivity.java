package com.yuzhuo.easyupbring.activity;

import com.yuzhuo.easyupbring.R;

import net.duohuo.dhroid.ioc.annotation.InjectView;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AboutUsActivity extends BaseAct {

	@InjectView(id = R.id.about_us_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_aboutus);
	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {
		case R.id.about_us_linear_back:
			finish();
			break;

		default:
			break;
		}
	}
}
