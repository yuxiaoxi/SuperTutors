package com.yuzhuo.easyupbring.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import org.json.JSONObject;

import android.R.integer;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.activity.AppStartActivity;
import com.yuzhuo.easyupbring.activity.MainActivity;
import com.yuzhuo.easyupbring.activity.SingleChatAct;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.EasyUpApplication;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.app.NoticeManager;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.bean.MessageData;
import com.yuzhuo.easyupbring.bean.MsgInfoBean;
import com.yuzhuo.easyupbring.bean.Notice;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

/**
 * 
 * @ClassName: LBService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 下午5:26:39
 * 
 */
public class LBService extends Service {

	private IBinder binder = new LBService.LocalBinder();
	public static boolean newMsg = false;
	public static boolean warnRed = false;
	public static Gson gson = new GsonBuilder()
			.enableComplexMapKeySerialization().create();
	public static boolean success;
	public static int syscounts;
	public static Set<Integer> systemSet = new HashSet<Integer>();
	public static Set<Integer> dynmicSet = new HashSet<Integer>();
	public LocationClient mLocationClient = null;
	// 定义三个字符串来存放地址经纬度
	public static String address = null;
	public static String latitude = null;
	public static String lontitude = null;
	public Runnable runnable = new loadDateThreah();
	// public ProgressDialog dialog = null;
	private int TIME = 500;
	private int firstime = 0;
	public String city, province;
	private CusPerference cusPerference;

	@Override
	public IBinder onBind(Intent intent) {

		return binder;
	}

	@Override
	public void onCreate() {
		Log.e("start", "service-start");

		cusPerference = new CusPerference();
		cusPerference.load();
		Thread thread = new Thread(runnable);
		thread.start();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public void onDestroy() {
		Log.e("stop", "service-stop");
		super.onDestroy();
	}

	// 定义内容类继承Binder
	public class LocalBinder extends Binder {
		// 返回本地服务
		LBService getService() {
			return LBService.this;
		}
	}

	/**
	 * 设置定位相关参数
	 */
	public void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setPoiNumber(100);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
	}

	class loadDateThreah implements Runnable {
		@Override
		public void run() {
			mLocationClient = ((EasyUpApplication) getApplication()).mLocationClient;
			setLocationOption();
			mLocationClient.start();
			address = ((EasyUpApplication) getApplication()).getAddress();
			latitude = ((EasyUpApplication) getApplication()).getLatitude();
			lontitude = ((EasyUpApplication) getApplication()).getLontitude();
			city = ((EasyUpApplication) getApplication()).getCity();
			province = ((EasyUpApplication) getApplication()).getProvince();
			handler.postDelayed(this, TIME);
			if (address == null || latitude == null || lontitude == null
					|| province == null || city == null) {

				if (firstime < 50) {
					firstime++;
					handler.sendEmptyMessage(1);
				} else {
					handler.sendEmptyMessage(0);
					mLocationClient.stop();
				}
			} else {
				handler.sendEmptyMessage(0);
				mLocationClient.stop();
			}

		}

	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				// Toast.makeText(LBService.this,
				// "您当前所在地址：" + address, Toast.LENGTH_SHORT).show();
				// dialog.cancel();
				AppContext.latitude = latitude;
				AppContext.longtude = lontitude;
				AppContext.address = address;
				AppContext.city = city;
				cusPerference.address = address;
				cusPerference.latitude = latitude;
				cusPerference.lontitude = lontitude;
				cusPerference.city = city;
				cusPerference.commit();
				handler.removeCallbacks(runnable);
			}
		}
	};

	private String getTime(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("HH:mm:ss");
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}
	
	/**
	 * 
	 * @Title: makeParam
	 * @Description: 上传文本转换成base64
	 * @param @param map
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String makeParam(Map<String, String> map) {
		JSONObject arr = null;
		String parameters;
		try {
			arr = new JSONObject(map);
			Log.e("json-->", arr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			parameters = new String(Base64.encode(gson.toJson(map).getBytes(),
					Base64.NO_WRAP));
			return URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

}
