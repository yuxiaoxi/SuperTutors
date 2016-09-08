package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UserInfoBean;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.service.MessageReceiveService;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @ClassName: AppStartActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 下午5:26:29
 * 
 */
public class AppStartActivity extends BaseAct {

	@Inject
	CusPerference cusPerference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startMessageService();
		setContentView(R.layout.act_start);
//		test();
		cusPerference.load();
		// Log.e("city", cusPerference.city);
		if (isEmpty(cusPerference.uid)) {
			Intent intent = new Intent(AppStartActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			if (cusPerference.logintype == 0) {
				doLogin(cusPerference.uid, cusPerference.password);
			} else {
				threeLogin(cusPerference.uid, cusPerference.threelogintype + "");
			}

		}

	}

	public void startMessageService() {
		Intent serviceIntent = new Intent(this, LBService.class);
		startService(serviceIntent);
	}
	
	public void startMessageReceiveService(){
		Intent serviceIntent = new Intent(this, MessageReceiveService.class);
		startService(serviceIntent);
	}

	void doLogin(final String uid, final String password) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("password", password);
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("login")
				+ "&data=" + LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("MJJ", "login ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("MJJ", "onErray");
			}

			@Override
			public void doInBackground(Response response) {
				super.doInBackground(response);
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				String code = JSONUtil.getString(response.jSON(), "data.code");
				response.addBundle("code", code);
				response.addBundle("message", message);
				transfer(response, 150);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (transfer == 150) {
					String message = response.getBundle("message");
					String code = response.getBundle("code");
					if ("1".equals(code)) {
						AppContext.username = cusPerference.uid;
						AppContext.uid = JSONUtil.getString(response.jSON(),
								"data.item.rid");
						AppContext.AVATAR = JSONUtil.getString(response.jSON(),
								"data.item.avatar");
						AppContext.CHARACTER = JSONUtil.getString(
								response.jSON(), "data.item.character");
						AppContext.USERNAME = JSONUtil.getString(
								response.jSON(), "data.item.username");
						Log.e("USERNAME", AppContext.USERNAME);
						getUserInfoById(AppContext.uid);
						// Intent intent = new Intent(AppStartActivity.this,
						// MainActivity.class);
						// startActivity(intent);
						// finish();
					} else {
						Intent intent = new Intent(AppStartActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
					Toast.makeText(AppStartActivity.this, message,
							Toast.LENGTH_SHORT).show();

				}
			}
		});

	}

	void getUserInfoById(final String rid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUserInfoById")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("MJJ", "login ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("MJJ", "onErray");
			}

			@Override
			public void doInBackground(Response response) {
				super.doInBackground(response);
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				String code = JSONUtil.getString(response.jSON(), "data.code");
				response.addBundle("code", code);
				response.addBundle("message", message);
				transfer(response, 150);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (transfer == 150) {
					Log.e("responseString", response.jSON().toString());
					String message = response.getBundle("message");
					String code = response.getBundle("code");
					if ("1".equals(code)) {
						AppContext.userInfoBean = response.modelFrom(
								UserInfoBean.class, "data.item");
						// Log.e("USERNAME",
						// AppContext.userInfoBean.getCollectnum());
						Intent intent = new Intent(AppStartActivity.this,
								MainActivity.class);
						startActivity(intent);
						startMessageReceiveService();
						finish();
					} 
					Toast.makeText(AppStartActivity.this, message,
							Toast.LENGTH_SHORT).show();

				}
			}
		});

	}

	public void test() {
		startProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", "中文");
		String url = "https://115.29.53.134:8443/bbg/test";
		String urlString = "http://115.29.53.134:8080/bbg/test";
		DhNet net = new DhNet(url);
		net.addParams(map);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doPost(new NetTask(this) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				Log.e("response------>", response.jSON().toString());

			}
		});

	}

	public void threeLogin(final String uid, String type) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("type", type);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("threeLogin")
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
					cusPerference.uid = uid;
					cusPerference.userName = JSONUtil.getString(
							response.jSON(), "data.item.rid");
					cusPerference.logintype = 1;
					cusPerference.commit();
					AppContext.username = uid;
					AppContext.uid = JSONUtil.getString(response.jSON(),
							"data.item.rid");
					AppContext.AVATAR = JSONUtil.getString(response.jSON(),
							"data.item.avatar");
					AppContext.CHARACTER = JSONUtil.getString(response.jSON(),
							"data.item.character");
					AppContext.USERNAME = JSONUtil.getString(response.jSON(),
							"data.item.username");

					getUserInfoById(AppContext.uid);

				} else {
					Intent intent = new Intent(AppStartActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
				stopProgressDialog();
				Toast.makeText(AppStartActivity.this,
						JSONUtil.getString(response.jSON(), "data.message"),
						Toast.LENGTH_SHORT).show();

			}
		});

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

}
