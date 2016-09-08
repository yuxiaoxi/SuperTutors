package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdviceActivity extends BaseAct {

	@InjectView(id = R.id.advice_edit_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.advice_btn_finish, click = "onClickCallBack")
	private TextView finishTextView;
	@InjectView(id = R.id.advice_edit_content)
	private EditText contentEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_advice);
	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {

		case R.id.advice_edit_linear_back:
			if (contentEditText.getText().toString().trim().length() > 0) {
				bombDialog();
			} else {
				finish();
			}

			break;
		case R.id.advice_btn_finish:
//			collectionFeedback(AppContext.userInfoBean.getUsername(),
//					contentEditText.getText().toString().trim());
			break;

		default:
			break;
		}
	}

	public void bombDialog() {
		AlertDialog.Builder builder1 = new Builder(AdviceActivity.this);
		builder1.setMessage("确定是否放弃编辑？");
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
						// 结束操作

						finish();
					}
				});

		builder1.create().show();
	}

	void collectionFeedback(final String username, final String feedback) {
		startProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		map.put("feedback", feedback);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("collectionFeedback");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				Log.e("responseString", response.plain());
				if ("29".equals(JSONUtil.getString(response.jSON(), "code"))) {
					Toast.makeText(context, "提交成功！感谢您的反馈，我们会尽快处理！",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(AdviceActivity.this,
							JSONUtil.getString(response.jSON(), "message"),
							Toast.LENGTH_SHORT).show();
				}
				stopProgressDialog();
			}
		});

	}
}
