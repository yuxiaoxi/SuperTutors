package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
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

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseAct implements PlatformActionListener {

	// 填写从短信SDK应用后台注册得到的APPKEY
	private static String APPKEY = "54b73d306e18";

	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "e4b6259487058994e93aa3262c12c477";

	private boolean ready;
	private Dialog pd;
	@InjectView(id = R.id.login_txt_regist, click = "onClickCallBack")
	private TextView login_txt_regist;
	@InjectView(id = R.id.registe_txt_login, click = "onClickCallBack")
	private TextView registe_txt_login;
	@InjectView(id = R.id.register_linear)
	private LinearLayout register_linear;
	@InjectView(id = R.id.login_linear)
	private LinearLayout login_linear;
	@InjectView(id = R.id.login_img_add_avatar)
	private ImageView login_img_add_avatar;
	@InjectView(id = R.id.login_txt_add_avatar)
	private TextView login_txt_add_avatar;
	@InjectView(id = R.id.sina_login, click = "onClickCallBack")
	private ImageView sina_login;
	@InjectView(id = R.id.weixin_login, click = "onClickCallBack")
	private ImageView weixin_login;
	@InjectView(id = R.id.qqzone_login, click = "onClickCallBack")
	private ImageView qqzone_login;
	@InjectView(id = R.id.registe_btn_getcode, click = "onClickCallBack")
	private TextView registe_btn_getcode;
	@InjectView(id = R.id.registe_btn_registe, click = "onClickCallBack")
	private TextView registe_btn_registe;
	@InjectView(id = R.id.login_edite_phone)
	private EditText login_edite_phone;
	@InjectView(id = R.id.login_edite_password)
	private EditText login_edite_password;
	@InjectView(id = R.id.login_btn_login, click = "onClickCallBack")
	private TextView login_btn_login;
	@InjectView(id = R.id.registe_edite_phone)
	private EditText registe_edite_phone;
	@InjectView(id = R.id.registe_edite_code)
	private EditText registe_edite_code;
	@InjectView(id = R.id.registe_edite_password)
	private EditText registe_edite_password;
	@InjectView(id = R.id.registe_edite_pswack)
	private EditText registe_edite_pswack;
	@Inject
	CusPerference cusPerference;
	private boolean isSendCode;
	private TimeCount time;
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR = 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	private boolean isRegister;
	private HashMap<String, Object> reses = new HashMap<String, Object>();
	private Platform platformsh;
	String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		initSDK();
		cusPerference.load();
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.login_txt_regist:
			login_linear.setVisibility(View.GONE);
			register_linear.setVisibility(View.VISIBLE);
//			login_img_add_avatar.setVisibility(View.VISIBLE);
//			login_txt_add_avatar.setVisibility(View.VISIBLE);
			break;

		case R.id.registe_txt_login:
			register_linear.setVisibility(View.GONE);
			login_linear.setVisibility(View.VISIBLE);
//			login_img_add_avatar.setVisibility(View.GONE);
//			login_txt_add_avatar.setVisibility(View.GONE);
			break;

		case R.id.login_btn_login:

			if (isEmpty(login_edite_phone.getText().toString().trim())
					|| isEmpty(login_edite_password.getText().toString().trim())) {
				Toast.makeText(this, "帐号或密码为空，请输入", Toast.LENGTH_SHORT).show();
			} else {
				doLogin(login_edite_phone.getText().toString().trim(),
						login_edite_password.getText().toString().trim());
			}

			break;

		case R.id.qqzone_login:
			authorize(new QQ(context));
			type = "3";
			break;

		case R.id.weixin_login:
			authorize(new Wechat(context));
			type = "2";
			break;

		case R.id.sina_login:
			authorize(new SinaWeibo(context));
			type = "1";
			break;

		case R.id.registe_btn_getcode:
			if (!isEmpty(registe_edite_phone.getText().toString().trim())) {
				SMSSDK.getVerificationCode("86", registe_edite_phone.getText()
						.toString().trim());
				int bg_color = getResources().getColor(R.color.app_style);
				registe_btn_getcode.setBackgroundColor(bg_color);
				time.start();
				isSendCode = true;
			} else {
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.registe_btn_registe:
			if (isEmpty(registe_edite_phone.getText().toString().trim())
					|| isEmpty(registe_edite_password.getText().toString()
							.trim())
					|| isEmpty(registe_edite_pswack.getText().toString().trim())
					|| isEmpty(registe_edite_code.getText().toString().trim())) {
				Toast.makeText(this, "输入信息有误，请填写完整！", Toast.LENGTH_SHORT)
						.show();
			} else {
				register(registe_edite_phone.getText().toString().trim(),
						registe_edite_pswack.getText().toString().trim(),
						registe_edite_pswack.getText().toString().trim(),
						registe_edite_code.getText().toString().trim());
			}
			break;
		default:
			break;
		}
	}

	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
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

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			registe_btn_getcode.setText("重新发送");
			registe_btn_getcode.setBackgroundResource(R.drawable.regetcode);
			registe_btn_getcode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			registe_btn_getcode.setClickable(false);
			registe_btn_getcode.setBackgroundResource(R.drawable.regetcode);
			registe_btn_getcode.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}

		if (plat.isValid()) {

			String userId = plat.getDb().getUserId();
			Log.e("userId--->", plat.getDb().getUserIcon());
			if (userId != null && !userId.equals("")) {
				handler.sendEmptyMessage(MSG_USERID_FOUND);
				login(plat.getName(), userId, plat.getDb().getUserIcon(), null);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}

	@Override
	public void onCancel(Platform arg0, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}

	}

	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			platformsh = platform;
			reses = res;
			handler.sendEmptyMessage(MSG_AUTH_COMPLETE);
		}

	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();

	}

	// 第三方登录
	private void login(String plat, String userId, String avatar,
			HashMap<String, Object> userInfo) {
		Log.d("MJJ", plat);
		if (plat.equals("QQ")) {
			type = "3";
		} else if (plat.equals("WeChat")) {
			type = "2";
		} else if (plat.equals("SinaWeibo")) {
			type = "1";
		}
		// personalService.threeLogin(type, userId, latitude, lontitude,
		// handler);
		AppContext.uid = userId;
		AppContext.type = type;
		if (userInfo != null) {
			threeRegister(userId, (String) userInfo.get("nickname"), avatar,
					type);
			// Log.e("userinfo----->", userInfo.toString());
			// AppContext.USERNAME = (String) userInfo.get("nickname");
		} else {
			Log.e("userinfo----->", "bbbbbbbbbbbbb");
			threeLogin(userId, type);
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_AUTH_COMPLETE) {
				//会出现当手机换了，第三方登录会出现重新注册报错的情况
				login(platformsh.getName(), platformsh.getDb().getUserId(), platformsh
						.getDb().getUserIcon(), reses);
			}

		};
	};

	/**
	 * 
	 * @Title: register
	 * @Description: 用户注册
	 * @param @param uid
	 * @param @param password
	 * @param @param ackpassword
	 * @param @param verification 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void register(final String uid, final String password,
			String ackpassword, String verification) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("password", password);
		map.put("ackpassword", ackpassword);
		map.put("verification", verification);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("register")
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
					isRegister = true;
					doLogin(uid, password);

				}
				Toast.makeText(LoginActivity.this,
						JSONUtil.getString(response.jSON(), "data.message"),
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	public void threeRegister(final String uid, String username, String avatar,
			final String type) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("username", username);
		map.put("avatar", avatar);
		map.put("type", type);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("threeRegister")
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
					isRegister = true;
					threeLogin(uid, type);

				}
				stopProgressDialog();
				Toast.makeText(LoginActivity.this,
						JSONUtil.getString(response.jSON(), "data.message"),
						Toast.LENGTH_SHORT).show();

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
					cusPerference.userName = JSONUtil.getString(response.jSON(),
							"data.item.rid");
					cusPerference.logintype = 1;
					cusPerference.threelogintype = 3;
					cusPerference.commit();
					AppContext.username = uid;
					AppContext.uid = JSONUtil.getString(response.jSON(),
							"data.item.rid");
					AppContext.AVATAR = JSONUtil.getString(response.jSON(),
							"data.item.avatar");
					AppContext.CHARACTER = JSONUtil.getString(
							response.jSON(), "data.item.character");
					AppContext.USERNAME = JSONUtil.getString(
							response.jSON(), "data.item.username");
					if (isRegister) {
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								SelectIdentityActivtiy.class);
						startActivity(intent);
						isRegister = false;
					} else {
						getUserInfoById(AppContext.uid);
//						Intent intent = new Intent(LoginActivity.this,
//								MainActivity.class);
//						startActivity(intent);
//						finish();
					}

				}else {
					
				}
				stopProgressDialog();
				Toast.makeText(LoginActivity.this,
						JSONUtil.getString(response.jSON(), "data.message"),
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	/**
	 * 
	 * @Title: doLogin
	 * @Description: 登录
	 * @param @param uid
	 * @param @param password 设定文件
	 * @return void 返回类型
	 * @throws
	 */
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
						cusPerference.uid = uid;
						cusPerference.userName = JSONUtil.getString(response.jSON(),
								"data.item.rid");
						cusPerference.password = password;
						cusPerference.logintype = 0;
						cusPerference.commit();
						AppContext.username = uid;
						AppContext.uid = JSONUtil.getString(response.jSON(),
								"data.item.rid");
						AppContext.AVATAR = JSONUtil.getString(response.jSON(),
								"data.item.avatar");
						AppContext.CHARACTER = JSONUtil.getString(
								response.jSON(), "data.item.character");
						AppContext.USERNAME = JSONUtil.getString(
								response.jSON(), "data.item.username");
						if (isRegister) {
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this,
									SelectIdentityActivtiy.class);
							startActivity(intent);
							finish();
							isRegister = false;
						} else {
							getUserInfoById(AppContext.uid);
							
						}
					}
					Toast.makeText(LoginActivity.this, message,
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
//						Log.e("USERNAME", AppContext.userInfoBean.getCollectnum());
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						startMessageReceiveService();
						finish();
					}
					Toast.makeText(LoginActivity.this, message,
							Toast.LENGTH_SHORT).show();

				}
			}
		});

	}
	
	public void startMessageReceiveService(){
		Intent serviceIntent = new Intent(this, MessageReceiveService.class);
		startService(serviceIntent);
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
