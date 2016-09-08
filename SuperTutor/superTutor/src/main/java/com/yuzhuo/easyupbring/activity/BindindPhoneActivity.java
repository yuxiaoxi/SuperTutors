package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

public class BindindPhoneActivity extends BaseAct {

	@InjectView(id = R.id.binding_phone_linear_back, click = "OnClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.binding_edite_phone)
	private EditText phoneEditText;
	@InjectView(id = R.id.binding_edite_code)
	private EditText codeEditText;
	@InjectView(id = R.id.binding_btn_getcode, click = "OnClickCallBack")
	private TextView getcodeTextView;
	@InjectView(id = R.id.bindding_btn_registe, click = "OnClickCallBack")
	private TextView bindingTextView;
	@InjectView(id = R.id.binding_phone_title)
	private TextView titleTextView;
	private TimeCount time;
	private boolean isSendCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_binding_phone);
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		if (AppContext.userInfoBean != null) {

			titleTextView
					.setText(AppContext.userInfoBean.getPhonenumber() != null
							&& !"".equals(AppContext.userInfoBean.getPhonenumber()) ? "修改绑定"
							: "绑定手机");
		}
		initSDK();
	}

	public void OnClickCallBack(View view) {
		switch (view.getId()) {
		case R.id.binding_phone_linear_back:
			finish();
			break;

		case R.id.binding_btn_getcode:
			if (!isEmpty(phoneEditText.getText().toString().trim())) {
				SMSSDK.getVerificationCode("86", phoneEditText.getText()
						.toString().trim());
				int bg_color = getResources().getColor(R.color.app_style);
				getcodeTextView.setBackgroundColor(bg_color);
				time.start();
				isSendCode = true;
			} else {
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.bindding_btn_registe:
			if (!isEmpty(phoneEditText.getText().toString().trim())
					&& !isEmpty(codeEditText.getText().toString().trim())) {
				getVerifyKey(AppContext.uid, AppContext.APPKEY, codeEditText
						.getText().toString().trim(), phoneEditText.getText()
						.toString().trim());
			} else {
				Toast.makeText(this, "填写信息不能为空！", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getcodeTextView.setText("重新发送");
			getcodeTextView.setBackgroundResource(R.drawable.regetcode);
			getcodeTextView.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getcodeTextView.setClickable(false);
			getcodeTextView.setBackgroundResource(R.drawable.regetcode);
			getcodeTextView.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	/**
	 * 
	 * @Title: isEmpty
	 * @Description: 判断字符串是否为空
	 * @param @param str
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, AppContext.APPKEY, AppContext.APPSECRET);
		final Handler handler = new Handler();
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {

			}
		};
		SMSSDK.unregisterEventHandler(eventHandler);
	}

	void getVerifyKey(final String uid, final String appkey, final String code,
			final String phoneNum) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", uid);
		map.put("appkey", appkey);
		map.put("code", code);
		map.put("phoneNum", phoneNum);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("verifyKey");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				Log.e("responseString", response.plain());
				if ("21".equals(JSONUtil.getString(response.jSON(), "code"))) {
					finish();
				}
				Toast.makeText(BindindPhoneActivity.this,
						JSONUtil.getString(response.jSON(), "message"),
						Toast.LENGTH_SHORT).show();
			}
		});

	}
}
